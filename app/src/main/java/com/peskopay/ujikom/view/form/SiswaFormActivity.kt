package com.peskopay.ujikom.view.form

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.peskopay.ujikom.R
import com.peskopay.ujikom.databinding.ActivitySiswaFormBinding
import com.peskopay.ujikom.utils.ValidateUtils
import com.peskopay.ujikom.view.main.DashboardActivity
import com.peskopay.ujikom.view.main.SiswaActivity
import com.ujikom.spp.myFunction.firestore.ModelDsp
import com.ujikom.spp.myFunction.firestore.ModelKelas
import com.ujikom.spp.myFunction.firestore.ModelSiswa
import kotlin.properties.Delegates

class SiswaFormActivity : AppCompatActivity() {
    private lateinit var edtNisn: EditText
    private lateinit var edtNis: EditText
    private lateinit var edtNama: EditText
    private lateinit var edtTelp: EditText
    private lateinit var edtAlamat: EditText
    private lateinit var spinnerDsp: Spinner
    private lateinit var spinnerKelas: Spinner
    private lateinit var btnCreate: Button
    private lateinit var btnUpdate: Button
    private lateinit var radioGroup: RadioGroup
    private lateinit var dspId: DocumentReference
    private lateinit var kelasId: DocumentReference
    private lateinit var radioButtonMale: RadioButton
    private lateinit var radioButtonFemale: RadioButton
    private var gender = true
    private var documentId = ""

    private var VIEW_CODE by Delegates.notNull<Boolean>()

    private lateinit var modelDsp: ModelDsp
    private lateinit var modelKelas: ModelKelas
    private lateinit var modelSiswa: ModelSiswa

    private lateinit var validateUtils: ValidateUtils
    private lateinit var binding: ActivitySiswaFormBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySiswaFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewUnit()

        btnCreate.setOnClickListener {
            val nisn = edtNisn.text.toString()
            val nis = edtNis.text.toString()
            val nama = edtNama.text.toString()
            val noTelp = edtTelp.text.toString()
            val alamat = edtAlamat.text.toString()
            val validate = validateInput(nisn, nis, nama, noTelp, alamat)
            if (validate) {
                modelSiswa.createSiswa(nisn, nis, nama, gender, alamat, noTelp, kelasId, dspId,
                    onSuccess = {
                        Toast.makeText(this, "Berhasil menambahkan data siswa baru.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SiswaFormActivity, SiswaActivity::class.java))
                        finish() },
                    onFailure = {
                        Toast.makeText(this, "Gagal menambahkan terjadi error yang tidak terduga.", Toast.LENGTH_SHORT).show()
                    })
            }
        }

        btnUpdate.setOnClickListener {
            val nisn = edtNisn.text.toString()
            val nis = edtNis.text.toString()
            val nama = edtNama.text.toString()
            val noTelp = edtTelp.text.toString()
            val alamat = edtAlamat.text.toString()
            val validate = validateInput(nisn, nis, nama, noTelp, alamat)
            if (validate) {
                modelSiswa.updateSiswa(documentId, nisn, nis, nama, gender, alamat, noTelp,
                onSuccess = {
                    Toast.makeText(this, "Berhasil merubah data siswa", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@SiswaFormActivity, SiswaActivity::class.java)
                    startActivity(intent)
                },
                onFailure = {
                    Toast.makeText(this, "Terjadi kesalahan yang tidak terduga?", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@SiswaFormActivity, SiswaActivity::class.java)
                    startActivity(intent)
                })
            }
        }
    }

    private fun validateInput(nisn: String, nis: String, namaLengkap: String,
                              noTelp: String, alamat: String): Boolean {

        val error = validateUtils.validateSiswa(nisn, nis, namaLengkap, noTelp, alamat, radioGroup)
        if (error != null) {
            when {
                error.contains("NISN") -> {
                    edtNisn.error = error
                    edtNisn.requestFocus()
                }
                error.contains("NIS") -> {
                    edtNis.error = error
                    edtNis.requestFocus()
                }
                error.contains("Nama") -> {
                    edtNama.error = error
                    edtNama.requestFocus()
                }
                error.contains("Nomor") -> {
                    edtTelp.error = error
                    edtTelp.requestFocus()
                }
                error.contains("Alamat") -> {
                    edtAlamat.error = error
                    edtAlamat.requestFocus()
                }
                error.contains("gender") -> {
                    radioButtonMale.error = error
                    radioButtonFemale.error = error
                    radioGroup.requestFocus()
                }
            }
            return false
        }
        return true
    }

    private fun viewUnit() {
        edtNisn = binding.edtNisn
        edtNis = binding.edtNis
        edtNama = binding.edtNamaLengkap
        edtTelp = binding.edtTelp
        edtAlamat = binding.edtAlamat
        spinnerDsp = binding.dsp
        spinnerKelas = binding.kelas
        btnCreate = binding.btnCreate
        btnUpdate = binding.btnUpdate
        radioButtonMale = binding.genderLaki
        radioButtonFemale = binding.genderPerempuan
        radioGroup = binding.genderGrup
        radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            val radioButton = radioGroup.findViewById<RadioButton>(i)
            gender = radioButton?.text.toString() == "Laki-laki"
        }

        modelDsp = ModelDsp()
        modelKelas = ModelKelas()
        modelSiswa = ModelSiswa()
        validateUtils = ValidateUtils()

        documentId = intent.getStringExtra("documentId").toString()
        VIEW_CODE = intent.getBooleanExtra("VIEW_CODE", false)
        if (VIEW_CODE){
            btnCreate.visibility = View.GONE
            binding.spinnerView.visibility = View.GONE
            btnUpdate.visibility = View.VISIBLE
            modelSiswa.getSiswaData(documentId,
            onSuccess = { siswaData ->
                for(siswa in siswaData) {
                    edtNisn.setText(siswa.nisn)
                    edtNis.setText(siswa.nis)
                    edtNama.setText(siswa.name)
                    edtAlamat.setText(siswa.alamat)
                    edtTelp.setText(siswa.noTelp)
                    if (siswa.jenisKelamin == "Laki-laki") {
                        radioGroup.check(R.id.genderLaki)
                    } else {
                        radioGroup.check(R.id.genderPerempuan)
                    }
                }
            },
            onFailure = { exception ->
                Toast.makeText(this, "Data tidak dapat ditemukan, terdapat kesalahan tidak terduga?", Toast.LENGTH_SHORT).show()
                onBackPressed()
                finish()
            })
        }

        modelDsp.getDspData(
            onSuccess = { dspData ->
                val dspAdapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    dspData.map { it.tahun }
                )

                dspAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerDsp.adapter = dspAdapter

                spinnerDsp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedDsp = dspData[position]
                        val documentId = selectedDsp.documentId
                        dspId = FirebaseFirestore.getInstance().collection("dsp").document(documentId)

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }
            },
            onFailure = { exception ->
                Toast.makeText(this, "Terjadi kesalahan saat mendapatkan data dsp.", Toast.LENGTH_SHORT).show()
                Log.w(ContentValues.TAG, "Error select dsp data:", exception)
            }
        )

        modelKelas.getKelasData(
            onSuccess = { kelasData ->
                val kelasAdapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    kelasData.map { it.namaKelas }
                )

                kelasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerKelas.adapter = kelasAdapter

                spinnerKelas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedKelas = kelasData[position]
                        val documentId = selectedKelas.documentId
                        kelasId = FirebaseFirestore.getInstance().collection("kelas").document(documentId)

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }
            },
            onFailure = { exception ->
                Toast.makeText(this, "Terjadi kesalahan saat mendapatkan data kelas.", Toast.LENGTH_SHORT).show()
                Log.w(ContentValues.TAG, "Error select kelas data:", exception)
            }
        )

    }
}