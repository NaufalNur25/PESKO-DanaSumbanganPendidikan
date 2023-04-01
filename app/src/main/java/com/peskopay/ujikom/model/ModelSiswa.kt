package com.ujikom.spp.myFunction.firestore

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.ujikom.spp.data.SiswaData
import javax.security.auth.callback.Callback

class ModelSiswa {

    companion object FirestoreHelper {
        private val db = FirebaseFirestore.getInstance()
    }

    fun getSiswaData(onSuccess: (List<SiswaData>) -> Unit, onFailure: (Exception) -> Unit) {
        val usersRef = db.collection("siswa")
        val siswaList = mutableListOf<SiswaData>()
        // Query all documents with "role" field set to "Siswa"
        usersRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    // Extract the necessary fields and create a SiswaListData object
                    val documentId = document.id
                    val nis = document.getString("nis") ?: ""
                    val name = document.getString("namaLengkap") ?: ""
                    val jenisKelamin = document.getString("jenisKelamin") ?: ""
                    val sisaBayar = document.getDouble("sisaBayar")!!.toInt()
                    siswaList.add(SiswaData(documentId, nis, name, jenisKelamin, sisaBayar))
                }
                onSuccess(siswaList)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    data class SiswaDataAll(
        val documentId: String,
        val nisn: String,
        val nis: String,
        val name: String,
        val noTelp: String,
        val alamat: String,
        val jenisKelamin: String,
        val sisaBayar: Int
    )
    fun getSiswaData(documentId: String, onSuccess: (List<SiswaDataAll>) -> Unit, onFailure: (Exception) -> Unit) {
        val siswaList = mutableListOf<SiswaDataAll>()
        db.collection("siswa").document(documentId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val documentId = document.id
                    val nisn = document.getString("nisn") ?: ""
                    val nis = document.getString("nis") ?: ""
                    val name = document.getString("namaLengkap") ?: ""
                    val noTelp = document.getString("noTelp") ?: ""
                    val alamat = document.getString("alamat") ?: ""
                    val jenisKelamin = document.getString("jenisKelamin") ?: ""
                    val sisaBayar = document.getDouble("sisaBayar")!!.toInt()
                    siswaList.add(SiswaDataAll(documentId, nisn, nis, name, noTelp, alamat, jenisKelamin, sisaBayar))
                    onSuccess(siswaList)
                }
                else {
                    onSuccess(emptyList())
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

//    fun getSiswaDataWithId(documentId: String, onSuccess: (SiswaData?) -> Unit, onFailure: (Exception) -> Unit) {
//        val siswaRef = db.collection("siswa").document(documentId)
//        val siswaList = mutableListOf<SiswaData>()
//        siswaList.clear()
//        siswaRef.get()
//            .addOnSuccessListener { document ->
//                if (document.exists()) {
//                    val documentId = document.id
//                    val nisn = document.getString("nisn") ?: ""
//                    val nis = document.getString("nis") ?: ""
//                    val name = document.getString("nama_lengkap") ?: ""
//                    val jenisKelamin = document.getString("jenis_kelamin") ?: ""
//                    val alamat = document.getString("alamat") ?: ""
//                    val no_telp = document.getString("no_telp")?: ""
//                    val kelas_id = document.getDocumentReference("kelas_id")
//                    val dsp_id = document.getDocumentReference("dsp_id")
//                    val sisaBayar = document.getString("sisaBayar") ?: ""
//                    siswaList.add(SiswaData(documentId, nisn, nis, name, jenisKelamin, alamat, no_telp, kelas_id, dsp_id, sisaBayar))
//                }
//                else {
//                    onSuccess(null)
//                }
//            }
//            .addOnFailureListener { exception ->
//                onFailure(exception)
//            }
//    }


//    fun getSiswaDataWithonKelas(kelasId: String, onSuccess: (List<SiswaKelas>) -> Unit, onFailure: (Exception) -> Unit) {
//        val kelasRef = db.collection("kelas").document(kelasId)
//        siswaList.clear()
//        db.collection("siswa").whereEqualTo("kelas_id", kelasRef).get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    if (document.exists()) {
//                        val documentId = document.id
//                        val nis = document.getString("nis") ?: ""
//                        val name = document.getString("nama_lengkap") ?: ""
//                        val sisaBayar = document.getString("sisaBayar")?: ""
//                        siswaList.add(SiswaKelas(documentId, nis, name, sisaBayar))
//                    } else {
//                        onSuccess(emptyList())
//                    }
//                }
//                onSuccess(siswaList)
//            }
//            .addOnFailureListener { exception ->
//                onFailure(exception)
//            }
//    }

    fun createSiswa(nisn: String, nis: String,
                    namaLengkap: String, isMale: Boolean,
                    alamat: String, noTelp: String,
                    kelasId: DocumentReference,
                    dspId: DocumentReference,
                    onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val siswa = hashMapOf(
            "nisn" to nisn,
            "nis" to nis,
            "namaLengkap" to namaLengkap.toUpperCase(),
            "jenisKelamin" to if (isMale) "Laki-laki" else "Perempuan",
            "alamat" to alamat,
            "noTelp" to noTelp,
            "kelasId" to kelasId,
            "dspId" to dspId,
            "userId" to null,
            "sisaBayar" to null,
        )
        db.collection("siswa")
            .add(siswa)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                createSisaBayar(documentReference.id,
                onSuccess = {
                    onSuccess()
                },
                onFailure = { e ->
                    Log.w(TAG, "Error adding document", e)
                    onFailure(e)
                })

            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                onFailure(e)
            }
    }

    fun updateSiswa(documentId: String, nisn: String, nis: String,
                    namaLengkap: String, isMale: Boolean,
                    alamat: String, noTelp: String,
                    onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userRef = db.collection("siswa").document(documentId)
        // Update only if the data is not null
        val updates = mutableMapOf<String, Any>()
        if (!nisn.isNullOrEmpty()) updates["nisn"] = nisn
        if (!nis.isNullOrEmpty()) updates["nis"] = nis
        if (!namaLengkap.isNullOrEmpty()) updates["namaLengkap"] = namaLengkap.uppercase()
        if (!alamat.isNullOrEmpty()) updates["alamat"] = alamat
        if (!noTelp.isNullOrEmpty()) updates["noTelp"] = noTelp
        updates["jenisKelamin"] = if (isMale) "Laki-laki" else "Perempuan"
        // Perform the update operation
        userRef.update(updates)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot with ID '$documentId' updated.")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document with ID '$documentId':", e)
                onFailure(e)
            }
    }

    fun updateSiswa(documentId: String, userId: DocumentReference) {
        val userRef = db.collection("siswa").document(documentId)
        val updates = mutableMapOf<String, Any>()
        if (userId != null) updates["userId"] = userId
        // Perform the update operation
        userRef.update(updates)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot with ID '$documentId' updated.")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document with ID '$documentId':", e)
            }
    }

    fun createSisaBayar(documentId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val siswaRef = db.collection("siswa").document(documentId)

        siswaRef.get().addOnSuccessListener { siswaDocumentSnapshot ->
            // Mendapatkan reference ke document DSP dari field "dsp" pada document siswa
            val dspRef = siswaDocumentSnapshot.get("dspId") as DocumentReference

            // Mengambil nilai nominal pada field "nominal" pada document DSP
            dspRef.get().addOnSuccessListener { dspDocumentSnapshot ->
                val nominal = dspDocumentSnapshot.getLong("nominal")
                if (nominal != null) {
                    val userRef = db.collection("siswa").document(documentId)
                    val updates = mutableMapOf<String, Any>()
                    updates["sisaBayar"] = nominal.toInt()
                    userRef.update(updates)
                        .addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot with ID '$documentId' CREATE sisa bayar.")
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error for create sisa bayar document with ID '$documentId':", e)
                            onFailure(e)
                        }
                }
            }.addOnFailureListener { exception ->
                Log.e(TAG, "Error getting DSP document: $exception")
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Error getting siswa document: $exception")
        }
    }

    fun updateSisaBayar(documentId: String, nominal: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit){
        val siswa = db.collection("siswa").document(documentId)

        val userRef = db.collection("siswa").document(documentId)
        val updates = mutableMapOf<String, Any>()
        if (nominal != null) updates["sisaBayar"] = nominal
        // Perform the update operation
        userRef.update(updates)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot with ID '$documentId' updated.")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document with ID '$documentId':", e)
                onFailure(e)
            }

    }

    fun deleteSiswa(documentId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val siswaRef = db.collection("siswa").document(documentId)
        siswaRef.delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

}