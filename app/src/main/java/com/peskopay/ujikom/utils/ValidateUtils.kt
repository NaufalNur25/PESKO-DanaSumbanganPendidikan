package com.peskopay.ujikom.utils

import android.util.Patterns
import android.widget.RadioGroup
import com.google.firebase.firestore.FirebaseFirestore

class ValidateUtils {
    fun inputNis(nis: String): String? {
        return when {
            nis.isEmpty() -> "NIS tidak boleh kosong!"
            nis.length < 8 -> "NIS minimal lebih dari 8 karakter!"
            else -> null
        }
    }

    fun inputNisn(nisn: String): String? {
        return when {
            nisn.isEmpty() -> "NISN tidak boleh kosong!"
            nisn.length < 9 -> "NISN minimal lebih dari 9 karakter!"
            else -> null
        }
    }

    fun inputTelp(noTelp: String): String? {
        val regex = "^8\\d{8,}$"
        return when {
            noTelp.isEmpty() -> "Nomor telepon tidak boleh kosong!"
            !noTelp.matches(regex.toRegex()) -> "Nomor telepon tidak sesuai!"
            else -> null
        }
    }

    fun inputNama(name: String): String? {
        return when {
            name.isEmpty() -> "Nama tidak boleh kosong!"
            else -> null
        }
    }

    fun inputTahun(tahun: String): String? {
        return when {
            tahun.isEmpty() -> "Tahun DSP tidak boleh kosong!"
            !tahun.matches("\\d+".toRegex()) -> "Tahun DSP harus berupa angka!"
            tahun.length != 4 -> "Tahun DSP harus terdiri dari 4 digit!"
            else -> null
        }
    }

    fun inputNominal(nominal: String): String? {
        return when {
            nominal.isEmpty() -> "Nominal DSP tidak boleh kosong!"
            !nominal.matches("\\d+".toRegex()) -> "Nominal harus berupa angka!"
            nominal.toInt() < 100000 -> "Nominal ini terlalu kecil untuk DSP"
            else -> null
        }
    }

    fun inputAlamat(alamat: String): String? {
        return when {
            alamat.isEmpty() -> "Alamat tidak boleh kosong!"
            else -> null
        }
    }

    fun inputEmail(email: String): String? {
        return when {
            email.isEmpty() -> "Email tidak boleh kosong!"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email tidak sesuai!"
            else -> null
        }
    }

    fun inputGender(gender: RadioGroup): String? {
        return when (gender.checkedRadioButtonId) {
            -1 -> "Siswa haru memimiliki gender!"
            else -> null
        }
    }

    fun inputKelas(nama_kelas: String): String? {
        return when {
            nama_kelas.isEmpty() -> "Nama Kelas tidak boleh kosong!"
            nama_kelas.length < 4 -> "Nama Kelas Minimal 4 karakter!"
            else -> null
        }
    }

    fun inputUsername(username: String): String? {
        val regex = "^(?=.*[A-Z])(?=.*\\d).+\$"

        val query = FirebaseFirestore.getInstance()
            .collection("users")
            .whereEqualTo("username", username)

        var isUsernameExists = false
        query.get().addOnSuccessListener { documents ->
            for (document in documents) {
                if (document.exists()) {
                    isUsernameExists = true
                    break
                }
            }
        }

        return when {

            username.isEmpty() -> "Username tidak bisa dikosongkan!"
            !username.matches(regex.toRegex()) -> "Username harus memiliki setidak huruf besar dan angka."
            username.length > 15 || username.length <= 5 -> "Username harus memiliki setidak +5 karakter dan tidak lebih dari +15 karakter"
            isUsernameExists ->  "Username sudah digunakan oleh pengguna lain."

            else -> null
        }
    }

    fun inputJurusan(jurusan: String): String? {
        return when {
            jurusan.isEmpty() -> "Jurusan tidak boleh kosong!"
            jurusan.length < 4 -> "Jurusan Minimal 10 karakter!"
            else -> null
        }
    }

    fun inputPassword(password: String): String? {
        return when {
            password.isEmpty() -> "Password tidak boleh kosong!"
            password.length < 6 -> "Password minimal 6 karakter!"
            else -> null
        }
    }

    fun inputBayar(jumlahBayar: String, sisaBayar: String): String? {
        return when {
            jumlahBayar.isEmpty() -> "Nominal tidak boleh kosong"
            jumlahBayar.toInt() <= 0 -> "Nominal harus lebih besar dari 0"
            jumlahBayar.toInt() > sisaBayar.toInt() -> "Nominal melebihi jumlah biaya yang perlu dibayar!"
            else -> null
        }
    }


    fun inputKonfirmasiPassword(password: String, konfirmasiPassword: String): String? {
        return when {
            konfirmasiPassword.isEmpty() -> "Konfirmasi Password tidak boleh kosong!"
            password != konfirmasiPassword -> "Password tidak sesuai"
            else -> null
        }
    }

    fun validateRegister(nis: String, username: String, email: String, password: String, konfirmasiPassword: String): String? {
        val nisError = inputNis(nis)
        if (nisError != null) return nisError

        val usernameError = inputUsername(username)
        if (usernameError != null) return usernameError

        val emailError = inputEmail(email)
        if (emailError != null) return emailError

        val passwordError = inputPassword(password)
        if (passwordError != null) return passwordError

        val konfirmasiPasswordError = inputKonfirmasiPassword(password, konfirmasiPassword)
        if (konfirmasiPasswordError != null) return konfirmasiPasswordError

        return null
    }

    fun validateLogin(email: String, password: String): String? {
        val emailError = inputEmail(email)
        if (emailError != null) return emailError

        val passwordError = inputPassword(password)
        if (passwordError != null) return passwordError

        return null
    }

    fun validateLogin(email: String): String? {
        val emailError = inputEmail(email)
        if (emailError != null) return emailError

        return null
    }

    fun validateSiswa(nisn: String, nis: String,
                      nama: String, noTelp: String,
                      alamat: String, gender: RadioGroup): String? {
        val nisnError = inputNisn(nisn)
        if (nisnError != null) return nisnError

        val nisError = inputNis(nis)
        if (nisError != null) return nisError

        val nameError = inputNama(nama)
        if (nameError != null) return nameError

        val noTelpError = inputTelp(noTelp)
        if (noTelpError != null) return noTelpError

        val alamatError = inputAlamat(alamat)
        if (alamatError != null) return alamatError

        val genderError = inputGender(gender)
        if (genderError != null) return genderError

        return null
    }

    fun validateKelas(nama_kelas: String, jurusan: String): String?{
        val kelasError = inputKelas(nama_kelas)
        if (kelasError != null) return kelasError

        val jurusanError = inputJurusan(jurusan)
        if (jurusanError != null) return jurusanError

        return null
    }

    fun validateDsp(tahun:String, nominal:String): String?{
        val dspError = inputTahun(tahun)
        if(dspError != null) return dspError

        val nominalError = inputNominal(nominal)
        if(nominalError != null) return nominalError
        return null
    }

    fun validatePetugas(username: String, email: String, password: String, konfirmasiPassword: String, namaLengkap: String): String?{
        val namaLengkapError = inputNama(namaLengkap)
        if(namaLengkapError != null) return namaLengkapError

        val usernameError = inputUsername(username)
        if (usernameError != null) return usernameError

        val emailError = inputEmail(email)
        if (emailError != null) return emailError

        val passwordError = inputPassword(password)
        if (passwordError != null) return passwordError

        val konfirmasiPasswordError = inputKonfirmasiPassword(password, konfirmasiPassword)
        if (konfirmasiPasswordError != null) return konfirmasiPasswordError

        return null
    }

    fun validatePembayaran(jumlahBayar: String, sisaBayar: String) : String? {

        val pembayaranError = inputBayar(jumlahBayar, sisaBayar)
        if(pembayaranError != null) return pembayaranError

        return null
    }
}