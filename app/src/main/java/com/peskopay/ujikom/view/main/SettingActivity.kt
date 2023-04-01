package com.peskopay.ujikom.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.peskopay.ujikom.databinding.ActivitySettingBinding
import com.peskopay.ujikom.view.auth.LoginActivity

class SettingActivity : AppCompatActivity() {
    private lateinit var logout: Button

    private lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewInit()

        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun viewInit() {
        logout = binding.btnLogout
    }
}