package com.ujikom.spp.myFunction.firestore

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.ujikom.spp.data.PetugasData

class ModelPetugas {
    companion object FirestoreHelper {
        private val db = FirebaseFirestore.getInstance()
    }

    fun getPetugasData(onSuccess: (List<PetugasData>) -> Unit, onFailure: (Exception) -> Unit) {
        val userRef = ModelPetugas.db.collection("users")
        val petugasList = mutableListOf<PetugasData>()
        // Query all documents with "role" field set to "petugas"
        userRef.whereEqualTo("role", "Petugas")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    // Extract the necessary fields and create a PetugasListData object
                    val documentId = document.id
                    val username = document.getString("username") ?: ""
                    val email = document.getString("email") ?: ""
                    petugasList.add(PetugasData(documentId, username, email, ""))
                }
                onSuccess(petugasList)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getPetugasIdOnAuth(onSuccess: (DocumentReference) -> Unit, onFailure: (Exception) -> Unit) {
        val userUid = FirebaseAuth.getInstance().currentUser?.uid

        if (userUid != null) {
            db.collection("petugas")
                .whereEqualTo("userId", db.collection("users").document(userUid))
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val documentId = documents.documents[0].id
                        val petugasRef = db.collection("petugas").document(documentId)
                        onSuccess(petugasRef)
                    } else {
                        onFailure(Exception("Dokumen petugas tidak ditemukan, pastikan kamu sudah login?"))
                    }
                }
                .addOnFailureListener { e ->
                    onFailure(e)
                }
        } else {
            onFailure(Exception("Pengguna belum masuk"))
        }
    }

    fun createPetugas(namaLengkap: String, userId: DocumentReference, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val petugas = hashMapOf(
            "namaLengkap" to namaLengkap,
            "userId" to userId,
        )
        db.collection("petugas")
            .add(petugas)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                onFailure(e)
            }
    }

//    fun updatePetugas(documentId: String, nama_lengkap: String) {
//
//
//        // Update only if the data is not null
//        val updates = mutableMapOf<String, Any>()
//        if (!nama_lengkap.isNullOrEmpty()) updates["nama_lengkap"] = nama_lengkap
//        // Perform the update operation
//        val petugas = ModelPetugas.db.collection("petugas")
//            .whereEqualTo("user_id", userRef).get()
//            .addOnSuccessListener { querySnapshot ->
//                for (document in querySnapshot){
//                    ModelPetugas.db.collection("petugas").document(document.id).update(updates)
//                        .addOnSuccessListener {
//                            Log.d(ContentValues.TAG, "DocumentSnapshot with ID '$documentId' updated.")
//                        }
//                        .addOnFailureListener { e ->
//                            Log.w(ContentValues.TAG, "Error updating document with ID '$documentId':", e)
//                        }
//                }
//            }
//    }

    fun deletePetugas(documentId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val petugasRef = ModelPetugas.db.collection("petugas").document(documentId)
        petugasRef.delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}