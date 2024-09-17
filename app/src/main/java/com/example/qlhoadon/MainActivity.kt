package com.example.qlhoadon

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    // khai báo toàn cục
    private lateinit var nutDN : Button
    private lateinit var nutDK : Button
    private lateinit var nutOut : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        // khai báo sd
        nutDN = findViewById(R.id.buttonDN)
        nutDK = findViewById(R.id.buttonDK)
        nutOut = findViewById(R.id.buttonOut)

        nutDN.setOnClickListener {
            // Chuyển activity
            val intent = Intent(this@MainActivity, DangNhapActivity::class.java)
            startActivity(intent)
        }

        nutDK.setOnClickListener {
            // Chuyển activity
            val intent = Intent(this@MainActivity, DangKyActivity::class.java)
            startActivity(intent)
        }

        nutOut.setOnClickListener {
            finishAffinity()
        }
    }
}
