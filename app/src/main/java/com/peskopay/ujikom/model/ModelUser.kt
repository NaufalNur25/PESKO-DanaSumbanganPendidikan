package com.peskopay.ujikom.model

import android.content.ContentValues.TAG
import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ModelUser {
    companion object FirestoreHelper {
        private val db = FirebaseFirestore.getInstance()
    }

    enum class UserRole {
        Admin,
        Petugas,
        Siswa,
    }

    fun createUser(uid: String, username: String, email: String, role: String): Task<Void> {
        val userRole = UserRole.values().find { it.name == role }
        if (userRole == null) {
            Log.e(TAG, "Invalid role: $role")
        }
        // Membuat dokumen baru di Firestore dengan UID sebagai referensi nilai
        val data = hashMapOf(
            "email" to email,
            "username" to username,
            "role" to role,
            "created_at" to Timestamp.now()
        )

        val usersCollection = db.collection("users")
        val userDocument = usersCollection.document(uid).set(data)
        return userDocument
    }

    data class CurentUser(val username: String, val role: String)
    fun CurentUserData(uid: String, onResult: (CurentUser) -> Unit) {
        // Membuat reference ke document users dengan ID tertentu
        // Mengambil document Siswa yang sesuai
        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                // Mendapatkan data username
                val username = document.getString("username")!!
                val role = document.getString("role")!!
                val curentUser = CurentUser(username, role)
                onResult(curentUser)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error: ", exception)
            }
    }
}