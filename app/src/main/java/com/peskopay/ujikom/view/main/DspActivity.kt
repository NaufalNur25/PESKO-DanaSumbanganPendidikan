package com.peskopay.ujikom.view.main

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.peskopay.ujikom.R
import com.peskopay.ujikom.databinding.ActivityDspBinding
import com.peskopay.ujikom.utils.ValidateUtils
import com.peskopay.ujikom.view.main.adapter.DspAdapter
import com.ujikom.spp.myFunction.firestore.ModelDsp

class DspActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageView
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var btnPopAdd: FloatingActionButton
    private lateinit var btnSubmit: Button

    private lateinit var edtTahun: EditText
    private lateinit var edtNominal: EditText
    private lateinit var dataCount: TextView

    private lateinit var builder: AlertDialog.Builder

    private var adapter: DspAdapter? = null
    private lateinit var rcView: RecyclerView
    private lateinit var modelDsp: ModelDsp

    private lateinit var validateUtils: ValidateUtils

    private lateinit var binding: ActivityDspBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDspBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewUnit()
        getUserData()

        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnPopAdd.setOnClickListener {
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.popup_form_dsp, null)
            edtTahun = dialogLayout.findViewById(R.id.edtTahun)
            edtNominal = dialogLayout.findViewById(R.id.edtNominal)
            btnSubmit = dialogLayout.findViewById(R.id.btnCreate)

            val dialog = builder.setView(dialogLayout)
                .create()
            dialog.show()

            btnSubmit.setOnClickListener {
                val tahun = edtTahun.text.toString()
                val nominal = edtNominal.text.toString()
                val validate = validateDsp(tahun, nominal)
                if (validate) {
                    modelDsp.createDsp(tahun.toInt(), nominal.toInt(),
                    onSuccess = {
                        dialog.dismiss()
                        Toast.makeText(this, "Berhasil membuat data dsp ${tahun}", Toast.LENGTH_SHORT).show()
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

    private fun validateDsp(tahun: String, nominal: String): Boolean {
        val error = validateUtils.validateDsp(tahun, nominal)
        if (error != null) {
            when {
                error.contains("Tahun") -> {
                    edtTahun.error = error
                    edtTahun.requestFocus()
                }
                error.contains("Nominal") -> {
                    edtNominal.error = error
                    edtNominal.requestFocus()
                }
            }
            return false
        }
        return true
    }

    private fun getUserData() {
        modelDsp.getDspData(
            onSuccess = { dspData ->
                adapter = DspAdapter(dspData)
                rcView.adapter = adapter
                adapter?.setOnClickDeleteItem {
                    AlertDialog.Builder(this)
                        .setTitle("Hapus data")
                        .setMessage("Apakah Anda yakin ingin menghapus data dsp ini?")
                        .setPositiveButton("Ya") { dialog, which ->
                            modelDsp.deleteDsp(it.documentId,
                                onSuccess = {
                                    Toast.makeText(this, "Berhasil menghapus dsp.", Toast.LENGTH_SHORT).show()
                                    adapter?.notifyDataSetChanged()
                                    getUserData()
                                },
                                onFailure = { exception ->
                                    Toast.makeText(this, "Terjadi kesalahan saat menghapus dsp.", Toast.LENGTH_SHORT).show()
                                    Log.w(ContentValues.TAG, "Error deleting dsp data:", exception)
                                }
                            )
                        }
                        .setNegativeButton("Batal") { dialog, which ->
                            // Pengguna menekan tombol "Tidak", tutup dialog
                            dialog.dismiss()
                        }
                        .show()
                }
                adapter?.setOnLongClickListener { dspData ->
                    val inflater = layoutInflater
                    val dialogLayout = inflater.inflate(R.layout.popup_form_dsp, null)
                    //declar item in popup
                    edtTahun = dialogLayout.findViewById(R.id.edtTahun)
                    edtTahun.setText(dspData.tahun.toString())
                    edtNominal = dialogLayout.findViewById(R.id.edtNominal)
                    edtNominal.setText(dspData.nominal.toString())
                    btnSubmit = dialogLayout.findViewById(R.id.btnCreate)

                    val dialog = builder.setView(dialogLayout).create()
                    dialog.show()

                    btnSubmit.setOnClickListener {
                        val tahun = edtTahun.text.toString()
                        val nominal = edtNominal.text.toString()

                        val validate = validateDsp(tahun, nominal)
                        if (validate) {
                            modelDsp.updateDsp(dspData.documentId, tahun.toInt(), nominal.toInt(),
                            onSuccess = {
                                dialog.dismiss()
                                Toast.makeText(this, "Berhasil mengubah data dsp ${tahun}", Toast.LENGTH_SHORT).show()
                                adapter?.notifyDataSetChanged()
                                getUserData()
                            },
                            onFailure = {
                                Toast.makeText(this, "Gagal merubah terjadi error yang tak terduga.", Toast.LENGTH_SHORT).show()
                            })
                        }
                    }
                }

                if (dspData.isNotEmpty()) {
                    rcView.visibility = View.VISIBLE
                    binding.tvNullItem.visibility = View.GONE
                } else {
                    rcView.visibility = View.GONE
                    binding.tvNullItem.visibility = View.VISIBLE
                }
                dataCount.text = (if (dspData.size > 9) "+9" else dspData.size).toString()
                adapter?.addItem(dspData)
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
        btnPopAdd = binding.fabButton

        rcView = binding.rcView
        rcView.layoutManager = LinearLayoutManager(this)
        rcView.setHasFixedSize(true)

        //builder dialog popup
        builder = AlertDialog.Builder(this)

        validateUtils = ValidateUtils()
        modelDsp = ModelDsp()
    }
}