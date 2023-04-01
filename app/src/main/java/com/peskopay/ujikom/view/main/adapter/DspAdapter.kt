package com.peskopay.ujikom.view.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.peskopay.ujikom.R
import com.ujikom.spp.data.DspData
import java.text.NumberFormat
import java.util.*

class DspAdapter(private var dspData: List<DspData>) : RecyclerView.Adapter<DspAdapter.ViewHolder>() {
    private var onClickLongItem: ((DspData) -> Unit)? = null
    private var onClickDeleteItem: ((DspData) -> Unit)? = null

    fun setOnLongClickListener(callbacks: (DspData) -> Unit) {
        onClickLongItem = callbacks
    }

    fun setOnClickDeleteItem(callbacks: (DspData)->Unit){
        this.onClickDeleteItem = callbacks
    }

    fun addItem(items: List<DspData>) {
        this.dspData = items
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_data, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dspData[position]
        holder.subText.text = data.tahun.toString()

        // format angka menjadi rupiah dengan pemisah ribuan dan desimal
        val formatter = NumberFormat.getInstance(Locale("id", "ID"))
        val formattedNominal = formatter.format(data.nominal)
        holder.titleText.text = "Rp " + formattedNominal

        holder.btnDelete.setOnClickListener { onClickDeleteItem?.invoke(data) }
        holder.itemView.setOnLongClickListener {
            onClickLongItem?.invoke(data)
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return dspData.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.titleText)
        val subText: TextView = itemView.findViewById(R.id.subText)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }
}
