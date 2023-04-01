package com.ujikom.spp.data

import com.google.firebase.firestore.DocumentReference

class TransaksiData (
    var documentId : String,
    var nis: Int,
    var tanggalBayar: String,
    var tahunDsp: Long,
    var jumlahBayar: Long,
    var petugas: DocumentReference?,
    var siswa: DocumentReference?
    )


