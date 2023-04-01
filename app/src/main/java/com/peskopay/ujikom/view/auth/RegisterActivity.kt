package com.peskopay.ujikom.view.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.peskopay.ujikom.R
import com.peskopay.ujikom.databinding.ActivityRegisterBinding
import com.peskopay.ujikom.model.auth.RegisterAuth
import com.peskopay.ujikom.utils.ValidateUtils

class RegisterActivity : AppCompatActivity() {
    private lateinit var login: TextView
    private lateinit var edtNis: EditText
    private lateinit var edtUsername: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtKonfPassword: EditText

    private lateinit var btnRegister: Button
    private lateinit var btnCheckBox: CheckBox

    private lateinit var validateUtils: ValidateUtils
    private lateinit var registerAuth: RegisterAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewUnit()

        login.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }

        btnRegister.setOnClickListener {
            val nis = edtNis.text.toString()
            val username = edtUsername.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            val konfirmasiPassword = edtKonfPassword.text.toString()
            val validate = validateInput(nis, username, email, password, konfirmasiPassword)

            if (validate) {
                db.collection("siswa")
                    .whereEqualTo("nis", edtNis.text.toString())
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.size() == 0) {
                            this.edtNis.error = "NIS tidak ditemukan, hubungi petugas untuk dibuatkan akun!"
                            this.edtNis.requestFocus()
                        } else {
                            registerAuth.registerUserSiswa(documents.documents[0].id, email, password, username)
                            Toast.makeText(this, "Berhasil membuat akun, silahkan login!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Error for get document", exception.toString())
                        // Login gagal, tampilkan pesan error
                        Toast.makeText(this, "Gagal membuat akun, sepertinya terdapat masalah?", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun validateInput(nis: String, username: String, email: String, password: String, konfirmasiPassword: String): Boolean {
        val error = validateUtils.validateRegister(nis, username, email, password, konfirmasiPassword)
        if (error != null) {
            when {
                error.contains("NIS") -> {
                    this.edtNis.error = error
                    this.edtNis.requestFocus()
                }
                error.contains("Username") -> {
                    this.edtUsername.error = error
                    this.edtUsername.requestFocus()
                }
                error.contains("Email") -> {
                    this.edtEmail.error = error
                    this.edtEmail.requestFocus()
                }
                error.contains("Password") -> {
                    this.edtPassword.error = error
                    this.edtPassword.requestFocus()
                }
                error.contains("Konfirmasi Password") -> {
                    this.edtKonfPassword.error = error
                    this.edtKonfPassword.requestFocus()
                }
            }
            return false
        }
        return true
    }

    private fun viewUnit() {
        validateUtils = ValidateUtils()
        db = FirebaseFirestore.getInstance()
        registerAuth = RegisterAuth()

        login = binding.login
        btnRegister = binding.buttonRegister

        edtNis = binding.inputNis
        edtUsername = binding.inputUsername
        edtEmail = binding.inputEmail
        edtPassword = binding.inputPassword
        edtKonfPassword = binding.inputKonfirmasiPassword

        btnCheckBox = binding.agree
        btnCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                btnRegister.setBackgroundResource(R.drawable.enabled_primary_button_background)
                btnRegister.isEnabled = true
                btnCheckBox.isEnabled = false
            } else {
                btnRegister.setBackgroundResource(R.drawable.disabled_primary_button_background)
                btnRegister.isEnabled = false
                btnCheckBox.isEnabled = true
            }
        }
    }
}