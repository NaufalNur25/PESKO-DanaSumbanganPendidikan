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
import com.peskopay.ujikom.databinding.ActivityPetugasBinding
import com.peskopay.ujikom.model.auth.RegisterAuth
import com.peskopay.ujikom.utils.ValidateUtils
import com.peskopay.ujikom.view.main.adapter.PetugasAdapter
import com.ujikom.spp.myFunction.firestore.ModelPetugas

class PetugasActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageView
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var btnPopAdd: FloatingActionButton
    private lateinit var btnSubmit: Button

    private lateinit var title: TextView
    private lateinit var dataCount: TextView
    private lateinit var edtUsername: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtKonfirmasiPassword: EditText
    private lateinit var edtNamaLengkap: EditText


    private lateinit var builder: AlertDialog.Builder
    private lateinit var modelPetugas: ModelPetugas
    private var adapter: PetugasAdapter? = null
    private lateinit var rcView: RecyclerView

    private lateinit var validateUtils: ValidateUtils
    private lateinit var registerAuth: RegisterAuth


    private lateinit var binding: ActivityPetugasBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPetugasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewUnit()
        getUserData()

        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnPopAdd.setOnClickListener {
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.popup_form_petugas, null)
            //declar item in popup
            edtUsername = dialogLayout.findViewById(R.id.edtUsername)
            edtEmail = dialogLayout.findViewById(R.id.edtEmail)
            edtPassword = dialogLayout.findViewById(R.id.edtPassword)
            edtKonfirmasiPassword = dialogLayout.findViewById(R.id.edtKonfirmasiPassword)
            edtNamaLengkap = dialogLayout.findViewById(R.id.edtNamaLengkap)
            btnSubmit = dialogLayout.findViewById(R.id.btnCreate)

            val dialog = builder.setView(dialogLayout)
                .create()
            dialog.show()

            btnSubmit.setOnClickListener {
                val username = edtUsername.text.toString()
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()
                val konfirmasiPassword = edtKonfirmasiPassword.text.toString()
                val namaLengkap = edtNamaLengkap.text.toString()
                val validate = validatePetugas(username, email, password, konfirmasiPassword, namaLengkap)
                if (validate) {
                    registerAuth.registerUserPetugas(email, password, username, namaLengkap) { valid ->
                        if (valid) {
                            dialog.dismiss()
                            Toast.makeText(this, "Berhasil membuat data petugas ${username}", Toast.LENGTH_SHORT).show()
                            adapter?.notifyDataSetChanged()
                            getUserData()
                        }

                    }
                }
            }
        }


    }

    private fun getUserData() {
        modelPetugas.getPetugasData(
            onSuccess = { petugasData ->
                adapter = PetugasAdapter(petugasData)
                rcView.adapter = adapter
                adapter?.setOnClickDeleteItem {
                    AlertDialog.Builder(this)
                        .setTitle("Hapus data")
                        .setMessage("Apakah Anda yakin ingin menghapus data dsp ini?")
                        .setPositiveButton("Ya") { dialog, which ->
                            modelPetugas.deletePetugas(it.documentId,
                                onSuccess = {
                                    Toast.makeText(this, "Berhasil menghapus petugas.", Toast.LENGTH_SHORT).show()
                                    adapter?.notifyDataSetChanged()
                                    getUserData()
                                },
                                onFailure = { exception ->
                                    Toast.makeText(this, "Terjadi kesalahan saat menghapus petugas.", Toast.LENGTH_SHORT).show()
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
                adapter?.setOnLongClickListener { petugasData ->
                    val inflater = layoutInflater
                    val dialogLayout = inflater.inflate(R.layout.popup_form_petugas, null)
                    //declar item in popup
                    edtUsername = dialogLayout.findViewById(R.id.edtUsername)
                    edtUsername.setText(petugasData.username)
                    edtEmail = dialogLayout.findViewById(R.id.edtEmail)
                    edtEmail.setText(petugasData.email)
                    btnSubmit = dialogLayout.findViewById(R.id.btnCreate)

                    val dialog = builder.setView(dialogLayout).create()
                    dialog.show()

//                    btnSubmit.setOnClickListener {
//                        val tahun = edtTahun.text.toString()
//                        val nominal = edtNominal.text.toString()
//
//                        val validate = validateDsp(tahun, nominal)
//                        if (validate) {
//                            modelDsp.updateDsp(dspData.documentId, tahun.toInt(), nominal.toInt(),
//                                onSuccess = {
//                                    dialog.dismiss()
//                                    Toast.makeText(this, "Berhasil mengubah data dsp ${tahun}", Toast.LENGTH_SHORT).show()
//                                    adapter?.notifyDataSetChanged()
//                                    getUserData()
//                                },
//                                onFailure = {
//                                    Toast.makeText(this, "Gagal merubah terjadi error yang tak terduga.", Toast.LENGTH_SHORT).show()
//                                })
//                        }
//                    }
                }

                if (petugasData.isNotEmpty()) {
                    rcView.visibility = View.VISIBLE
                    binding.tvNullItem.visibility = View.GONE
                } else {
                    rcView.visibility = View.GONE
                    binding.tvNullItem.visibility = View.VISIBLE
                }
                dataCount.text = (if (petugasData.size > 9) "+9" else petugasData.size).toString()
                adapter?.addItem(petugasData)
            },
            onFailure = { exception ->
                Log.w(ContentValues.TAG, "Error getting petugas data:", exception)
            }
        )
    }

    private fun validatePetugas(username: String,
                                email: String,
                                password: String,
                                konfirmasiPassword: String,
                                namaLengkap: String): Boolean {
        val error = validateUtils.validatePetugas(username, email, password, konfirmasiPassword, namaLengkap)
        if (error != null) {
            when {
                error.contains("Nama") -> {
                    edtNamaLengkap.error = error
                    edtNamaLengkap.requestFocus()
                }
                error.contains("Username") -> {
                    edtUsername.error = error
                    edtUsername.requestFocus()
                }
                error.contains("Email") -> {
                    edtEmail.error = error
                    edtEmail.requestFocus()
                }
                error.contains("Password") -> {
                    edtPassword.error = error
                    edtPassword.requestFocus()
                }
                error.contains("Konfirmasi Password") -> {
                    edtKonfirmasiPassword.error = error
                    edtKonfirmasiPassword.requestFocus()
                    clearPassword()
                }
            }
            return false
        }
        return true
    }

    private fun clearPassword() {
        edtPassword.text.clear()
        edtKonfirmasiPassword.text.clear()
        edtPassword.requestFocus()
    }

    private fun viewUnit() {
        appBarLayout = binding.appBarLayout
        btnBack = appBarLayout.findViewById(R.id.btnBack)
        dataCount = appBarLayout.findViewById(R.id.itemCount)
        btnPopAdd = binding.fabButton
        title = appBarLayout.findViewById(R.id.titleHeader)
        title.text = "Kelola Petugas"
        rcView = binding.rcView
        rcView.layoutManager = LinearLayoutManager(this)
        rcView.setHasFixedSize(true)

        //builder dialog popup
        builder = AlertDialog.Builder(this)
        modelPetugas = ModelPetugas()

        validateUtils = ValidateUtils()
        registerAuth = RegisterAuth()
    }
}