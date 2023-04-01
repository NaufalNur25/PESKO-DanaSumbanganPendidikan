package com.peskopay.ujikom.view.main

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.peskopay.ujikom.R
import com.peskopay.ujikom.databinding.ActivityKelasBinding
import com.peskopay.ujikom.utils.ValidateUtils
import com.peskopay.ujikom.view.main.adapter.DspAdapter
import com.peskopay.ujikom.view.main.adapter.KelasAdapter
import com.ujikom.spp.myFunction.firestore.ModelDsp
import com.ujikom.spp.myFunction.firestore.ModelKelas

class KelasActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageView
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var btnPopAdd: FloatingActionButton

    private lateinit var edtKelas: EditText
    private lateinit var edtJurusan: EditText
    private lateinit var btnSubmit: Button

    private lateinit var title: TextView
    private lateinit var dataCount: TextView

    private lateinit var builder: AlertDialog.Builder

    private var adapter: KelasAdapter ?= null
    private lateinit var rcView: RecyclerView
    private lateinit var modelKelas: ModelKelas
    private lateinit var validateUtils: ValidateUtils

    private lateinit var binding: ActivityKelasBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKelasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewUnit()
        getUserData()

        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnPopAdd.setOnClickListener {
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.popup_form_kelas, null)
            //declar item in popup
            edtKelas = dialogLayout.findViewById(R.id.edtKelas)
            edtJurusan = dialogLayout.findViewById(R.id.edtJurusan)
            btnSubmit = dialogLayout.findViewById(R.id.btnCreate)

            val dialog = builder.setView(dialogLayout)
                .create()
            dialog.show()

            btnSubmit.setOnClickListener {
                val kelas = edtKelas.text.toString()
                val jurusan = edtJurusan.text.toString()
                val validate = validateKelas(kelas, jurusan)
                if (validate) {
                    modelKelas.createKelas(kelas, jurusan,
                    onSuccess = {
                        dialog.dismiss()
                        Toast.makeText(this, "Berhasil membuat data kelas ${kelas}", Toast.LENGTH_SHORT).show()
                        adapter?.notifyDataSetChanged()
                        getUserData()
                    },
                    onFailure = {
                        Toast.makeText(this, "Gagal menambahkan terjadi error yang tidak terduga.", Toast.LENGTH_SHORT).show()
                    })
                }
            }
        }

    }

    private fun validateKelas(kelas: String, jurusan: String): Boolean {
        val error = validateUtils.validateKelas(kelas, jurusan)
        if (error != null) {
            when {
                error.contains("Kelas") -> {
                    edtKelas.error = error
                    edtKelas.requestFocus()
                }
                error.contains("Jurusan") -> {
                    edtJurusan.error = error
                    edtJurusan.requestFocus()
                }
            }
            return false
        }
        return true
    }

    private fun getUserData() {
        modelKelas.getKelasData(
            onSuccess = { kelasData ->
                adapter = KelasAdapter(kelasData)
                rcView.adapter = adapter
                adapter?.setOnClickDeleteItem {
                    AlertDialog.Builder(this)
                        .setTitle("Hapus data")
                        .setMessage("Apakah Anda yakin ingin menghapus data kelas ini?")
                        .setPositiveButton("Ya") { dialog, which ->
                            modelKelas.deleteKelas(it.documentId,
                                onSuccess = {
                                    Toast.makeText(this, "Berhasil menghapus kelas.", Toast.LENGTH_SHORT).show()
                                    adapter?.notifyDataSetChanged()
                                    getUserData()
                                },
                                onFailure = { exception ->
                                    // Lakukan sesuatu jika terjadi kesalahan dalam penghapusan data
                                    Toast.makeText(this, "Terjadi kesalahan saat menghapus kelas.", Toast.LENGTH_SHORT).show()
                                    Log.w(ContentValues.TAG, "Error deleting kelas data:", exception)
                                }
                            )
                        }
                        .setNegativeButton("Batal") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
                }
                adapter?.setOnLongClickListener { kelasData ->
                    val inflater = layoutInflater
                    val dialogLayout = inflater.inflate(R.layout.popup_form_kelas, null)

                    edtKelas = dialogLayout.findViewById(R.id.edtKelas)
                    edtKelas.setText(kelasData.namaKelas)
                    edtJurusan = dialogLayout.findViewById(R.id.edtJurusan)
                    edtJurusan.setText(kelasData.jurusan)
                    btnSubmit = dialogLayout.findViewById(R.id.btnCreate)

                    val dialog = builder.setView(dialogLayout)
                        .create()
                    dialog.show()

                    btnSubmit.setOnClickListener {
                        val kelas = edtKelas.text.toString()
                        val jurusan = edtJurusan.text.toString()

                        val validate = validateKelas(kelas, jurusan)
                        if (validate) {
                            modelKelas.updateKelas(kelasData.documentId, kelas, jurusan,
                            onSuccess = {
                                dialog.dismiss()
                                Toast.makeText(this, "Berhasil merubah data kelas ${kelas}", Toast.LENGTH_SHORT).show()
                                adapter?.notifyDataSetChanged()
                                getUserData()
                            },
                            onFailure = {
                                Toast.makeText(this, "Gagal merubah data terjadi error yang tak terduga.", Toast.LENGTH_SHORT).show()
                            })
                        }
                    }
                }

                if (kelasData.isNotEmpty()) {
                    rcView.visibility = View.VISIBLE
                    binding.tvNullItem.visibility = View.GONE
                } else {
                    rcView.visibility = View.GONE
                    binding.tvNullItem.visibility = View.VISIBLE
                }
                dataCount.text = (if (kelasData.size > 9) "+9" else kelasData.size).toString()
                adapter?.addItem(kelasData)
            },
            onFailure = { exception ->
                Log.w(ContentValues.TAG, "Error getting dsp data:", exception)
            }
        )
    }

    private fun viewUnit() {
        appBarLayout = binding.appBarLayout
        btnBack = appBarLayout.findViewById(R.id.btnBack)
        dataCount = appBarLayout.findViewById(R.id.itemCount)
        title = appBarLayout.findViewById(R.id.titleHeader)
        title.text = "Kelola Kelas Siswa"

        btnPopAdd = binding.fabButton

        rcView = binding.rcView
        rcView.layoutManager = LinearLayoutManager(this)
        rcView.setHasFixedSize(true)

        //builder dialog popup
        builder = AlertDialog.Builder(this)

        validateUtils = ValidateUtils()
        modelKelas = ModelKelas()
    }
}