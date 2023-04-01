package com.peskopay.ujikom.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.peskopay.ujikom.R
import com.peskopay.ujikom.model.ModelUser
import com.peskopay.ujikom.model.auth.LoginAuth
import com.peskopay.ujikom.view.auth.LoginActivity
import com.peskopay.ujikom.view.main.DashboardActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var loginAuth: LoginAuth
    private lateinit var modelUser: ModelUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        loginAuth = LoginAuth()
        modelUser = ModelUser()

        // Buat handler untuk menampilkan splash screen selama beberapa detik
        Handler().postDelayed({
            val currentUser = loginAuth.getAuth().currentUser?.uid
            if (currentUser == null){
                // Pindah ke halaman utama setelah splash screen selesai
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000) //splash screen ditampilkan selama 3 detik
    }

    override fun onStart() {
        super.onStart()

        val currentUser = loginAuth.getAuth().currentUser?.uid
        if (currentUser != null) {
            val uid = currentUser
            modelUser.CurentUserData(uid) { userData ->
                val intent = Intent(this, DashboardActivity::class.java).apply {
                    putExtra("username", userData.username)
                    putExtra("role", userData.role)
                    Toast.makeText(this@SplashActivity, "Selamat Datang ${FirebaseAuth.getInstance().currentUser?.email}", Toast.LENGTH_SHORT).show()
                    finish()
                }
                startActivity(intent)
            }
        }
    }
}