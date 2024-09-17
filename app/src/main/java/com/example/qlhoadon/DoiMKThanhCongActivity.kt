package com.example.qlhoadon

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class DoiMKThanhCongActivity : AppCompatActivity() {
    // khai báo toàn cục
    private lateinit var nutOK : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doi_mk_thanhcong)
        // khai báo sd
        nutOK = findViewById(R.id.buttonDone)
        nutOK.setOnClickListener {
            val intent = Intent(this, DangNhapActivity::class.java)
            startActivity(intent)
        }
    }
}