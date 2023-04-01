package com.peskopay.ujikom.model.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.peskopay.ujikom.model.ModelUser
import com.ujikom.spp.myFunction.firestore.ModelPetugas
import com.ujikom.spp.myFunction.firestore.ModelSiswa

class RegisterAuth() {
    companion object FirebaseHelper {
        private val mAuth = FirebaseAuth.getInstance()
        private val db = FirebaseFirestore.getInstance()
        private val modelUser = ModelUser()
        private val modelSiswa = ModelSiswa()
        private val modelPetugas = ModelPetugas()
    }

    fun registerUserSiswa(siswaId:String, email: String, password: String , username: String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = mAuth.currentUser!!.uid

                    modelUser.createUser(uid, username, email, "Siswa")
                        .addOnSuccessListener { documentReference ->
                            val userId = db.collection("users").document(uid)
                            modelSiswa.updateSiswa(siswaId, userId)
                        }
                        .addOnFailureListener{ exception ->
                            Log.e("RegisterActivity", "ModelUserCreateUser failed", exception)
                        }
                }else{
                    val exception = task.exception
                    Log.e("RegisterActivity", "createUserWithEmailAndPassword failed", exception)
                }
            }
    }

    fun registerUserPetugas(email: String,
                            password: String ,
                            username: String,
                            namaLengkap: String,
                            callback: (Boolean) -> Unit){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = mAuth.currentUser!!.uid

                    modelUser.createUser(uid, username, email, "Petugas")
                        .addOnSuccessListener { documentReference ->
                            val userId = db.collection("users").document(uid)
                            modelPetugas.createPetugas(namaLengkap, userId,
                            onSuccess = {
                                callback(true)
                            },
                            onFailure = {
                                callback(false)
                            })

                        }
                        .addOnFailureListener{ exception ->
                            Log.e("RegisterActivity", "ModelUserCreateUser failed", exception)
                            callback(false)
                        }
                }else{
                    val exception = task.exception
                    Log.e("RegisterActivity", "createUserWithEmailAndPassword failed", exception)
                    callback(false)
                }
            }
    }

}
