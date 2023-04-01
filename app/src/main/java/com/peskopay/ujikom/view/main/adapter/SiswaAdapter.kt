package com.peskopay.ujikom.view.main.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.peskopay.ujikom.R
import com.ujikom.spp.data.SiswaData
import java.text.NumberFormat
import java.util.*

class SiswaAdapter(private var siswaData: List<SiswaData>, private val parentView: View) : RecyclerView.Adapter<SiswaAdapter.ViewHolder>() {
    private var onClickLongItem: ((View, SiswaData) -> Unit)? = null
    private var onClickDeleteItem: ((SiswaData) -> Unit)? = null

    fun setOnLongClickListener(callbacks: (View, SiswaData) -> Unit) {
        onClickLongItem = callbacks
    }

    fun setOnClickDeleteItem(callbacks: (SiswaData)->Unit){
        this.onClickDeleteItem = callbacks
    }

    fun addItem(items: List<SiswaData>) {
        this.siswaData = items
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_data_siswa, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = siswaData[position]

        // format angka menjadi rupiah dengan pemisah ribuan dan desimal
        val formatter = NumberFormat.getInstance(Locale("id", "ID"))
        val formattedNominal = formatter.format(data.sisaBayar)
//        holder.titleText.text = "Rp " + formattedNominal
        holder.titleText.text = data.name
        holder.subText.text = data.nis
        holder.sisaBayar.text = if (data.sisaBayar <= 0) "Lunas" else "Rp " + formattedNominal

        holder.itemView.setOnLongClickListener { view ->
            onClickLongItem?.invoke(view, data)
            return@setOnLongClickListener true
        }
    }

//    private fun showPopupMenu(view: View, data: SiswaData) {
//        val popup = PopupMenu(view.context, view)
//        popup.inflate(R.menu.setting_crud_menu)
//        popup.setOnMenuItemClickListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.menu_pembayaran -> {
//                    // aksi saat menu Edit diklik
//                    true
//                }
//                R.id.menu_edit -> {
//                    // aksi saat menu Edit diklik
//                    true
//                }
//                R.id.menu_delete -> {
//
//                    true
//                }
//                else -> false
//            }
//        }
//        popup.show()
//
////        val location = IntArray(2)
////        view.getLocationOnScreen(location)
////        val x = location[0] + view.width
////        val y = location[1] + view.height / 2
////        popup.showAtLocation(parentView, Gravity.NO_GRAVITY, x, y)
//    }

    override fun getItemCount(): Int {
        return siswaData.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.titleText)
        val subText: TextView = itemView.findViewById(R.id.subText)
        val sisaBayar: TextView = itemView.findViewById(R.id.sisaBayar)
    }
}