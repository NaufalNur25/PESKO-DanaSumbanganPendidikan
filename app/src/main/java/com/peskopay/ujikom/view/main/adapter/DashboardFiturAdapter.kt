package com.peskopay.ujikom.view.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.peskopay.ujikom.R

data class ItemData(
    val titleText: String, val subText: String, val imageHeader: Int
)
class FiturAdapter(private val items: ArrayList<ItemData>) : RecyclerView.Adapter<FiturAdapter.ViewHolder>() {
    private var onClickItem: ((ItemData) -> Unit)? = null

    fun setOnClickItem(listener: (ItemData) -> Unit) {
        onClickItem = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_fitur_dashboard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items[position]
        holder.imageHeader.setImageResource(currentItem.imageHeader)
        holder.titleText.text = currentItem.titleText
        holder.subText.text = currentItem.subText

        holder.itemView.setOnClickListener {
            onClickItem?.invoke(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.titleText)
        val subText: TextView = itemView.findViewById(R.id.subText)
        val imageHeader: ShapeableImageView = itemView.findViewById(R.id.imageHeader)
    }
}
