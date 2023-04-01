package com.peskopay.ujikom.view.auth

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.peskopay.ujikom.R
import com.peskopay.ujikom.databinding.ActivityLoginBinding
import com.peskopay.ujikom.model.ModelUser
import com.peskopay.ujikom.model.auth.LoginAuth
import com.peskopay.ujikom.utils.PasswordUtils
import com.peskopay.ujikom.utils.ValidateUtils
import com.peskopay.ujikom.view.main.DashboardActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var register: TextView
    private lateinit var forget: TextView
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText

    private lateinit var btnLogin: Button
    private lateinit var btnShowPassword: CheckBox

    private lateinit var validateUtils: ValidateUtils
    private lateinit var loginAuth: LoginAuth
    private lateinit var modelUser: ModelUser

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        unitView()

        register.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        forget.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.popup_reset_password, null)
            val edtVerifikasi = dialogLayout.findViewById<EditText>(R.id.edtVerifikasi)
            val btnVerifikasi = dialogLayout.findViewById<Button>(R.id.btnVerifiaksi)

            val dialog = builder.apply {
                setTitle("Reset Password")
                setMessage("Enter your email to reset your password")
                setView(dialogLayout)
                setNegativeButton("Cancel") { dialog, which ->
                    // Code to handle cancel button click
                    dialog.cancel()
                }
                show()
            }.create()

            btnVerifikasi.setOnClickListener {
                val error = validateUtils.validateLogin(edtVerifikasi.text.toString())
                if (error != null) {
                    when {
                        error.contains("Email") -> {
                            edtVerifikasi.error = error
                            edtVerifikasi.requestFocus()
                        }
                    }
                }else{
                    loginAuth.forgetPassword(edtVerifikasi.text.toString()) { success, errorMessage ->
                        if (success) {
                            Toast.makeText(
                                this,
                                "Password reset email has been sent to $edtVerifikasi",
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(
                                this,
                                "Password reset failed: $errorMessage",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            val validate = validateInput(email, password)
            if (validate) {
                loginAuth.loginUser(edtEmail.text.toString(), edtPassword.text.toString()){ success, errorMessage ->
                    if (success) {
                        val currentUser = loginAuth.getAuth().currentUser
                        if (currentUser != null) {
                            val uid = currentUser.uid
                            modelUser.CurentUserData(uid) { userData ->
                                val intent = Intent(this, DashboardActivity::class.java).apply {
                                    putExtra("username", userData.username)
                                    putExtra("role", userData.role)
                                    Toast.makeText(this@LoginActivity, "Selamat Datang ${FirebaseAuth.getInstance().currentUser?.email}", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                startActivity(intent)
                            }
                        }
                    } else {
                        // Login gagal, tampilkan pesan error
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        val error = validateUtils.validateLogin(email, password)
        if (error != null) {
            when {
                error.contains("Email") -> {
                    edtEmail.error = error
                    edtEmail.requestFocus()
                }
                error.contains("Password") -> {
                    edtPassword.error = error
                    edtPassword.requestFocus()
                }
            }
            clearPassword()
            return false
        }
        return true
    }

    private fun clearPassword() {
        edtPassword.text.clear()
    }

    private fun unitView() {
        edtEmail = binding.inputEmail
        edtPassword = binding.inputPassword

        btnLogin = binding.buttonLogin
        btnShowPassword = binding.showPassword
        btnShowPassword.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                binding.lockIcon.visibility = View.GONE
                binding.unlockIcon.visibility = View.VISIBLE
            }else if (!isChecked){
                binding.lockIcon.visibility = View.VISIBLE
                binding.unlockIcon.visibility = View.GONE
            }
        }
        PasswordUtils.setupPasswordVisibilityToggle(
            edtPassword,
            btnShowPassword
        )

        register = binding.register
        forget = binding.textForgotPassword

        validateUtils = ValidateUtils()
        loginAuth = LoginAuth()
        modelUser = ModelUser()
    }
}