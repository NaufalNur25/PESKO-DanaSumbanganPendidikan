package com.peskopay.ujikom.view.form

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.peskopay.ujikom.databinding.ActivityPembayaranFormBinding
import com.peskopay.ujikom.model.auth.LoginAuth
import com.peskopay.ujikom.utils.ValidateUtils
import com.peskopay.ujikom.view.main.DashboardActivity
import com.peskopay.ujikom.view.main.SiswaActivity
import com.ujikom.spp.myFunction.firestore.ModelKelas
import com.ujikom.spp.myFunction.firestore.ModelPetugas
import com.ujikom.spp.myFunction.firestore.ModelSiswa
import com.ujikom.spp.myFunction.firestore.ModelTransaksi
import java.text.NumberFormat
import java.util.*

class PembayaranFormActivity : AppCompatActivity() {
    private var documentId = ""
    private var sisaBayarSiswa = ""
    private lateinit var nis: TextView
    private lateinit var nama: TextView
    private lateinit var jenisKelamin: TextView
    private lateinit var sisaBayar: TextView
    private lateinit var edtNominal: EditText
    private lateinit var btnBayar: Button

    private lateinit var validateUtils: ValidateUtils

    private lateinit var modelSiswa: ModelSiswa
    private lateinit var modelPetugas: ModelPetugas
    private lateinit var loginAuth: LoginAuth
    private lateinit var modelTransaksi: ModelTransaksi

    private lateinit var binding: ActivityPembayaranFormBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPembayaranFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewUnit()

        btnBayar.setOnClickListener {
            val nominal = edtNominal.text.toString()
            val validate = validateInput(nominal)

            val siswaRef = FirebaseFirestore.getInstance().collection("siswa").document(documentId)
            modelPetugas.getPetugasIdOnAuth(
                onSuccess = { petugasRef ->
                    if (validate) {
                        modelTransaksi.createTransaksi(nominal.toInt(), petugasRef, siswaRef) { valid ->
                            if (valid){
                                val nominalSisaBayar = sisaBayarSiswa.toInt() - edtNominal.text.toString().toInt()
                                modelSiswa.updateSisaBayar(documentId, nominalSisaBayar,
                                    onSuccess = {
                                        Toast.makeText(this, "Berhasil membuat transaksi", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, SiswaActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    },
                                    onFailure = {
                                        Toast.makeText(this, "Terjadi kesalah yang tidak terduga?", Toast.LENGTH_SHORT).show()
                                    })
                            }
                        }
                    }
                },
                onFailure = { exception ->
                    Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun validateInput(nominal: String): Boolean {
        val error = validateUtils.validatePembayaran(nominal, sisaBayarSiswa)
        if (error != null) {
            when {
                error.contains("Nominal") -> {
                    this.edtNominal.error = error
                    this.edtNominal.requestFocus()
                }
            }
            return false
        }
        return true
    }

    private fun viewUnit() {
        documentId = intent.getStringExtra("documentId").toString()
        nis = binding.tvNis
        nama = binding.tvName
        jenisKelamin = binding.tvJenisKelamin
        sisaBayar = binding.sisaBayar
        btnBayar = binding.btnBayar

        edtNominal = binding.edtNominal

        validateUtils = ValidateUtils()
        modelSiswa = ModelSiswa()
        modelPetugas = ModelPetugas()
        modelTransaksi = ModelTransaksi()
        loginAuth = LoginAuth()

        modelSiswa.getSiswaData(documentId,
            onSuccess = { data ->
                for (siswaData in data) {
                    val formatter = NumberFormat.getInstance(Locale("id", "ID"))
                    val formattedNominal = formatter.format(siswaData.sisaBayar)
                    nis.text = siswaData.nis
                    nama.text = siswaData.name
                    jenisKelamin.text = siswaData.jenisKelamin
                    sisaBayar.text = "Rp " + formattedNominal
                    sisaBayarSiswa = siswaData.sisaBayar.toString()
                }
            },
            onFailure = {
                Toast.makeText(this, "Maaf, terjadi kesalah saat mengambil data?", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@PembayaranFormActivity, DashboardActivity::class.java))
                finish()
            })
    }
}