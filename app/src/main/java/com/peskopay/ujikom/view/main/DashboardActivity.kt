package com.peskopay.ujikom.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.peskopay.ujikom.R
import com.peskopay.ujikom.databinding.ActivityDashboardBinding
import com.peskopay.ujikom.view.main.adapter.FiturAdapter
import com.peskopay.ujikom.view.main.adapter.ItemData

class DashboardActivity : AppCompatActivity() {
    private lateinit var rcViewFitur: RecyclerView

    private var role = ""
    private var username = ""

    private lateinit var tvUsername: TextView
    private lateinit var mainHeader: AppBarLayout

    lateinit var itemData: ArrayList<ItemData>
    private lateinit var binding: ActivityDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewUnit()

        binding.btnBayar.setOnClickListener {
            val intent = Intent(this, SiswaActivity::class.java)
            intent.putExtra("role", role)
            startActivity(intent)
        }
    }

    private fun startIntent(itemData: ItemData){
        when (itemData.titleText) {
            "Kelola DSP" -> {
                val intent = Intent(this, DspActivity::class.java)
                startActivity(intent)
            }
            "Kelola Kelas" -> {
                val intent = Intent(this, KelasActivity::class.java)
                startActivity(intent)
            }
            "Kelola Siswa" -> {
                val intent = Intent(this, SiswaActivity::class.java)
                intent.putExtra("role", role)
                startActivity(intent)
            }
            "Kelola Petugas" -> {
                val intent = Intent(this, PetugasActivity::class.java)
                startActivity(intent)
            }
            else -> {
                Toast.makeText(this, "Error, Data offsite?", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun viewUnit() {
        username = intent.getStringExtra("username").toString()
        role = intent.getStringExtra("role").toString()

        mainHeader = binding.header
        mainHeader.findViewById<TextView>(R.id.roleName).text = role
        mainHeader.findViewById<ImageView>(R.id.imageProfile).setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        tvUsername = binding.tvUsername
        tvUsername.text = "Hello, ${username}"

        rcViewFitur = binding.rcViewFitur
        rcViewFitur.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rcViewFitur.setHasFixedSize(true)

        itemData = ArrayList()
        var adapter = FiturAdapter(itemData)
        itemData.add(ItemData("Kelola DSP", "Buat DSP pertahun", R.drawable.ic_dsp))
        itemData.add(ItemData("Kelola Kelas", "Buat kelas untuk siswa", R.drawable.ic_kelas))
        itemData.add(ItemData("Kelola Siswa", "Buat data siswa baru", R.drawable.ic_student))
        itemData.add(ItemData("Kelola Petugas", "Buat akses untuk petugas", R.drawable.ic_stuff))
        rcViewFitur.adapter = adapter

        adapter.setOnClickItem { itemData ->
            startIntent(itemData)
        }

        if (role == "Petugas") {
            binding.alert.visibility = View.GONE
            binding.rcViewFitur.visibility = View.GONE
        }

        if (role == "Siswa") {
            binding.alert.visibility = View.GONE
            binding.rcViewFitur.visibility = View.GONE
            binding.byrSekarang.visibility = View.GONE
        }

        if (role == "Admin") {
            binding.byrSekarang.visibility = View.GONE
        }
    }
}