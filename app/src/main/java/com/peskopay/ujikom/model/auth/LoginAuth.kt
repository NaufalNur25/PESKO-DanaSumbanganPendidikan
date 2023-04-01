package com.peskopay.ujikom.model.auth

import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginAuth() {
    companion object FirebaseHelper {
        private val mAuth = FirebaseAuth.getInstance()
    }

    fun loginUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        System.out.println(password)
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun forgetPassword(email: String, onComplete: (Boolean, String?) -> Unit) {
        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }


    fun getAuth(): FirebaseAuth {
        return mAuth
    }
}