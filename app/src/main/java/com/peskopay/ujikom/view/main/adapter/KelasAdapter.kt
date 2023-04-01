package com.peskopay.ujikom.view.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.peskopay.ujikom.R
import com.ujikom.spp.data.KelasData

class KelasAdapter(private var kelasData: List<KelasData>) : RecyclerView.Adapter<KelasAdapter.ViewHolder>() {
    private var onClickLongItem: ((KelasData) -> Unit)? = null
    private var onClickDeleteItem: ((KelasData) -> Unit)? = null

    fun setOnLongClickListener(callbacks: (KelasData) -> Unit) {
        onClickLongItem = callbacks
    }

    fun setOnClickDeleteItem(callbacks: (KelasData)->Unit){
        this.onClickDeleteItem = callbacks
    }

    fun addItem(items: List<KelasData>) {
        this.kelasData = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KelasAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_data, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: KelasAdapter.ViewHolder, position: Int) {
        val data = kelasData[position]
        holder.titleText.text = data.namaKelas
        holder.subText.text = data.jurusan
        holder.btnDelete.setOnClickListener { onClickDeleteItem?.invoke(data) }
        holder.itemView.setOnLongClickListener {
            onClickLongItem?.invoke(data)
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return kelasData.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.titleText)
        val subText: TextView = itemView.findViewById(R.id.subText)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }
}