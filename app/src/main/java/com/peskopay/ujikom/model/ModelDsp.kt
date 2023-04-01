package com.ujikom.spp.myFunction.firestore

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.ujikom.spp.data.DspData

class ModelDsp {
    companion object FirestoreHelper {
        private val db = FirebaseFirestore.getInstance()
    }

    fun getDspData(onSuccess: (List<DspData>) -> Unit, onFailure: (Exception) -> Unit) {
        val dspRef = ModelDsp.db.collection("dsp")
        val dspList = mutableListOf<DspData>()
        // Query all documents with "role" field set to "Dsp"
        dspRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    // Extract the necessary fields and create a DspListData object
                    val documentId = document.id
                    val tahun = document.getLong("tahun")?.toInt() ?: 0
                    val nominal = document.getLong("nominal")?.toInt() ?: 0
                    dspList.add(DspData(documentId, tahun, nominal))
                }
                onSuccess(dspList)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun createDsp(tahun: Int, nominal: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val dsp = hashMapOf(
            "tahun" to tahun,
            "nominal" to nominal
        )
        db.collection("dsp")
            .add(dsp)
            .addOnSuccessListener { documentReference ->
                onSuccess()
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding document", exception)
                onFailure(exception)
            }
    }

    fun updateDsp(documentId: String, tahun: Int, nominal: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userRef = ModelDsp.db.collection("dsp").document(documentId)
        // Update only if the data is not null
        val updates = mutableMapOf<String, Any>()
        if (tahun >=0) updates["tahun"] = tahun
        if (nominal >=0) updates["nominal"] = nominal
        // Perform the update operation
        userRef.update(updates)
            .addOnSuccessListener {
                onSuccess()
                Log.d(ContentValues.TAG, "DocumentSnapshot with ID '$documentId' updated.")
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error updating document with ID '$documentId':", exception)
                onFailure(exception)
            }
    }

    fun deleteDsp(documentId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val dspRef = ModelDsp.db.collection("dsp").document(documentId)
        dspRef.delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}