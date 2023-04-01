package com.peskopay.ujikom.view.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.peskopay.ujikom.R
import com.ujikom.spp.data.PetugasData

class PetugasAdapter(private var petugasData: List<PetugasData>) : RecyclerView.Adapter<PetugasAdapter.ViewHolder>() {
    private var onClickLongItem: ((PetugasData) -> Unit)? = null
    private var onClickDeleteItem: ((PetugasData) -> Unit)? = null

    fun setOnLongClickListener(callbacks: (PetugasData) -> Unit) {
        onClickLongItem = callbacks
    }

    fun setOnClickDeleteItem(callbacks: (PetugasData)->Unit){
        this.onClickDeleteItem = callbacks
    }

    fun addItem(items: List<PetugasData>) {
        this.petugasData = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetugasAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_data, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = petugasData[position]
        holder.titleText.text = data.username
        holder.subText.text = data.username
        holder.btnDelete.setOnClickListener { onClickDeleteItem?.invoke(data) }
        holder.itemView.setOnLongClickListener {
            onClickLongItem?.invoke(data)
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return petugasData.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.titleText)
        val subText: TextView = itemView.findViewById(R.id.subText)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }
}