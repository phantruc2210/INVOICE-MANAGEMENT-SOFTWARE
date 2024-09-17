package com.example.qlhoadon.QuanLyEdit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.qlhoadon.DatabaseHelper
import com.example.qlhoadon.LopProduct.LoaiSanPham
import com.example.qlhoadon.LopProduct.SanPham
import com.example.qlhoadon.R

class EditLoaiSPActivity : AppCompatActivity() {
    // khai báo toàn cục
    private lateinit var tenLSP: EditText
    private lateinit var ghiChu: EditText
    private lateinit var toolbarLSP : Toolbar
    private lateinit var databaseHelper: DatabaseHelper

    private lateinit var btnSuaLSP: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_loaisp)
        // khai báo sử dụng
        tenLSP = findViewById(R.id.tenlsp_edit)
        ghiChu = findViewById(R.id.ghichu_lsp_edit)
        btnSuaLSP = findViewById(R.id.btnSuaLSP)
        toolbarLSP = findViewById(R.id.toolbarLSP)
        databaseHelper = DatabaseHelper(this)

        setSupportActionBar(toolbarLSP)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "LOẠI SẢN PHẨM"
        toolbarLSP.setNavigationOnClickListener { finish() }

        val LoaisanPham = intent.getSerializableExtra("loaiSanPham") as LoaiSanPham

        val maLSP = LoaisanPham.maloaiSP
        tenLSP.setText(LoaisanPham.tenloaiSP)
        ghiChu.setText(LoaisanPham.ghiChu)

        btnSuaLSP.setOnClickListener {
            val namelsp = tenLSP.text.toString()
            val note = ghiChu.text.toString()
            if(namelsp.isNotEmpty())
            {
                databaseHelper.updateLSP(maLSP, namelsp, note)
                Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show()
                // Gửi broadcast để cập nhật dữ liệu
                val intent = Intent("UPDATE_LSP")
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                finish()  // Kết thúc activity sau khi gửi broadcast
            }
            else
            {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
            }
            finish()
        }

    }
}