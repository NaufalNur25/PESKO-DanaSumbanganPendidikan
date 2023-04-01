package com.ujikom.spp.myFunction.firestore

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.ujikom.spp.data.KelasData

class ModelKelas {
    companion object FirestoreHelper {
        private val db = FirebaseFirestore.getInstance()
    }

    fun getKelasData(onSuccess: (List<KelasData>) -> Unit, onFailure: (Exception) -> Unit) {
        val usersRef = ModelKelas.db.collection("kelas")
        val kelasList = mutableListOf<KelasData>()
        // Query all documents with "role" field set to "Siswa"
        usersRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    // Extract the necessary fields and create a KelasListData object
                    val documentId = document.id
                    val namaKelas = document.getString("namaKelas") ?: ""
                    val jurusan = document.getString("jurusan") ?: ""
                    kelasList.add(KelasData(documentId, namaKelas, jurusan))
                }
                onSuccess(kelasList)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun createKelas(namaKelas: String, jurusan: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val kelas = hashMapOf(
            "namaKelas" to namaKelas.toUpperCase(),
            "jurusan" to jurusan.toUpperCase()
        )
        ModelKelas.db.collection("kelas")
            .add(kelas)
            .addOnSuccessListener { documentReference ->
                onSuccess()
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error adding document", exception)
                onFailure(exception)
            }
    }

    fun updateKelas(documentId: String, namaKelas: String, jurusan: String,
                    onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userRef = ModelKelas.db.collection("kelas").document(documentId)
        // Update only if the data is not null
        val updates = mutableMapOf<String, Any>()
        if (!namaKelas.isEmpty() ) updates["namaKelas"] = namaKelas
        if (!jurusan.isEmpty()) updates["jurusan"] = jurusan
        // Perform the update operation
        userRef.update(updates)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot with ID '$documentId' updated.")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error updating document with ID '$documentId':", e)
                onFailure(e)
            }
    }

    fun deleteKelas(documentId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val kelasRef = ModelKelas.db.collection("kelas").document(documentId)
        kelasRef.delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}