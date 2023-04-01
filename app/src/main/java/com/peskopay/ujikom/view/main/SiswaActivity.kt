package com.peskopay.ujikom.view.main

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.peskopay.ujikom.R
import com.peskopay.ujikom.databinding.ActivitySiswaBinding
import com.peskopay.ujikom.utils.ValidateUtils
import com.peskopay.ujikom.view.form.PembayaranFormActivity
import com.peskopay.ujikom.view.form.SiswaFormActivity
import com.peskopay.ujikom.view.main.adapter.DspAdapter
import com.peskopay.ujikom.view.main.adapter.SiswaAdapter
import com.ujikom.spp.myFunction.firestore.ModelDsp
import com.ujikom.spp.myFunction.firestore.ModelKelas
import com.ujikom.spp.myFunction.firestore.ModelSiswa

class SiswaActivity : AppCompatActivity() {
    private lateinit var btnAdd: FloatingActionButton
    private lateinit var btnBack: ImageView
    private lateinit var builder: AlertDialog.Builder
    private lateinit var search: EditText

    private lateinit var validateUtils: ValidateUtils
    private lateinit var modelSiswa: ModelSiswa
    private lateinit var modelKelas: ModelKelas
    private lateinit var modelDsp: ModelDsp

    private var adapter: SiswaAdapter? = null
    private lateinit var rcView: RecyclerView

    private lateinit var binding: ActivitySiswaBinding
    private lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySiswaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewUnit()
        getUserData()

        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnAdd.setOnClickListener {
            val intent = Intent(this@SiswaActivity, SiswaFormActivity::class.java)
            intent.putExtra("VIEW_CODE", false)
            startActivity(intent)
        }
    }

    private fun getUserData() {
        modelSiswa.getSiswaData(
            onSuccess = { siswaData ->
                adapter = SiswaAdapter(siswaData, rcView)
                rcView.adapter = adapter
                adapter?.setOnLongClickListener { view, siswaData ->
                    val popup = PopupMenu(view.context, view)
                    popup.inflate(R.menu.setting_crud_menu)
                    popup.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.menu_pembayaran -> {
                                val role = intent.getStringExtra("role").toString()
                                if (role != "Petugas") {
                                    Toast.makeText(this, "Kamu ${role} tidak memiliki hak untuk melakukan pembayaran!", Toast.LENGTH_SHORT).show()
                                }else{
                                    if (siswaData.sisaBayar != 0){
                                        val intent = Intent(this@SiswaActivity, PembayaranFormActivity::class.java)
                                        intent.putExtra("documentId", siswaData.documentId)
                                        startActivity(intent)
                                    }else{
                                        Toast.makeText(this, "Siswa sudah melunasi pembayaran Dspnya", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                true
                            }
                            R.id.menu_edit -> {
                                val intent = Intent(this@SiswaActivity, SiswaFormActivity::class.java)
                                intent.putExtra("documentId", siswaData.documentId)
                                intent.putExtra("VIEW_CODE", true)
                                startActivity(intent)
                                true
                            }
                            R.id.menu_delete -> {
                                AlertDialog.Builder(this)
                                    .setTitle("Hapus data")
                                    .setMessage("Apakah Anda yakin ingin menghapus data siswa ini?")
                                    .setPositiveButton("Ya") { dialog, which ->
                                        modelSiswa.deleteSiswa(siswaData.documentId,
                                            onSuccess = {
                                                Toast.makeText(this, "Berhasil menghapus data siswa.", Toast.LENGTH_SHORT).show()
                                                adapter?.notifyDataSetChanged()
                                                getUserData()
                                            },
                                            onFailure = { exception ->
                                                Toast.makeText(this, "Terjadi kesalahan saat menghapus siswa ini.", Toast.LENGTH_SHORT).show()
                                                Log.w(ContentValues.TAG, "Error deleting dsp data:", exception)
                                            }
                                        )
                                    }
                                    .setNegativeButton("Batal") { dialog, which ->
                                        // Pengguna menekan tombol "Tidak", tutup dialog
                                        dialog.dismiss()
                                    }
                                    .show()
                                true
                            }
                            else -> false
                        }
                    }
                    popup.show()
                }
                if (siswaData.isNotEmpty()) {
                    rcView.visibility = View.VISIBLE
                    binding.tvNullItem.visibility = View.GONE
                } else {
                    rcView.visibility = View.GONE
                    binding.tvNullItem.visibility = View.VISIBLE
                }
                if (siswaData.size > 8) {
                    binding.searchBar.visibility = View.VISIBLE
                }

                adapter?.addItem(siswaData)
            },
            onFailure = { exception ->
                Log.w(ContentValues.TAG, "Error getting dsp data:", exception)
            }
        )
    }

    private fun viewUnit() {
        btnAdd = binding.fabButton
        btnBack = binding.btnBack
        search = binding.search
        builder = AlertDialog.Builder(this)

        rcView = binding.rcView
        rcView.layoutManager = LinearLayoutManager(this)
        rcView.setHasFixedSize(true)
        context = this

        validateUtils = ValidateUtils()
        modelSiswa = ModelSiswa()
        modelKelas = ModelKelas()
        modelDsp = ModelDsp()
    }
}