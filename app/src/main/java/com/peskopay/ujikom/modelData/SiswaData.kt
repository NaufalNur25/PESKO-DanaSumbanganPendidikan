package com.ujikom.spp.data

import com.google.firebase.firestore.DocumentReference

data class SiswaData(
    val documentId: String,
    val nis: String,
    val name: String,
    val jenisKelamin: String,
    val sisaBayar: Int
)
