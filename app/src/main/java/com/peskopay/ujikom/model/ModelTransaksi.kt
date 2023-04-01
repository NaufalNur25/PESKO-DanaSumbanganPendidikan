package com.ujikom.spp.myFunction.firestore

import android.content.ContentValues
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference
import com.ujikom.spp.data.TransaksiData
import java.util.*

class ModelTransaksi {

    companion object FirestoreHelper {
        private val db = FirebaseFirestore.getInstance()
    }

    fun getTransaksiData(onSuccess: (List<TransaksiData>) -> Unit, onFailure: (Exception) -> Unit) {
        val transaksiRef = db.collection("documentId")
        val transaksiList = mutableListOf<TransaksiData>()
        // Query all documents with "role" field set to "transaksi"
        transaksiRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    // Extract the necessary fields and create a SiswaListData object
                    val documentId = document.id
                    val nis = document.getLong("nis")?.toInt() ?: 0
                    val tanggal_bayar = document.getString("tanggal_bayar")?: ""
                    val jumlah_bayar = document.getLong("jumlah_bayar")?: 0
                    val tahunDsp = document.getLong("id_dsp") ?: 0
                    val petugas = document.getDocumentReference("id_petugas")
                    val siswa = document.getDocumentReference("id_siswa")
                    transaksiList.add(TransaksiData(documentId, nis, tanggal_bayar, tahunDsp, jumlah_bayar, petugas, siswa))
                }
                onSuccess(transaksiList)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getSiswaWithTransaksi(siswaId:String,onSuccess: (List<TransaksiData>) -> Unit, onFailure: (Exception) -> Unit) {
        val transaksiRef = db.collection("siswa").document(siswaId)
        val transaksiList = mutableListOf<TransaksiData>()
        db.collection("transaksi").whereEqualTo("siswaId", transaksiRef).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.exists()) {
                        val documentId = document.id
                        val nis = document.getLong("nis")?.toInt() ?: 0
                        val tanggal_bayar = document.getString("tanggal_bayar")?: ""
                        val jumlah_bayar = document.getLong("jumlah_bayar")?: 0
                        val tahunDsp = document.getLong("id_dsp")?: 0
                        val petugas = document.getDocumentReference("id_petugas")
                        val siswa = document.getDocumentReference("id_siswa")
                        transaksiList.add(TransaksiData(documentId, nis, tanggal_bayar, tahunDsp, jumlah_bayar, petugas, siswa))
                    } else {
                        onSuccess(emptyList())
                    }
                }
                onSuccess(transaksiList)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }

    }

    fun createTransaksi(jumlahBayar: Int, petugasId: DocumentReference, siswaId: DocumentReference, callback: (Boolean) -> Unit) {
        val calendar = Calendar.getInstance() // membuat objek Calendar untuk mendapatkan tanggal saat ini
        val transaksi = hashMapOf(
            "tanggalBayar" to Timestamp(calendar.time),
            "jumlahBayar" to jumlahBayar,
            "tahunDsp" to calendar.get(Calendar.YEAR),
            "siswaId" to siswaId,
            "petugasId" to petugasId
        )

        db.collection("transaksi")
            .add(transaksi)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
                callback(false)
            }
    }
}