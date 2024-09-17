package com.example.qlhoadon.QuanLyEdit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.qlhoadon.DatabaseHelper
import com.example.qlhoadon.LopProduct.KhachHang
import com.example.qlhoadon.MainHomeActivity
import com.example.qlhoadon.R

class EditKhachHangActivity : AppCompatActivity() {
    // khai báo toàn cục
    private lateinit var tenKH: EditText
    private lateinit var diaChiKH: EditText
    private lateinit var ngaySinhKH: EditText
    private lateinit var dienThoaiKH: EditText
    private lateinit var emailKH: EditText
    private lateinit var co: RadioButton
    private lateinit var khong: RadioButton
    private lateinit var btnSuaKH: Button

    private lateinit var toolbarKH : Toolbar

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_khachhang)
        // khai báo sử dụng
        tenKH = findViewById(R.id.tenkh_edit)
        diaChiKH = findViewById(R.id.diachikh_edit)
        ngaySinhKH = findViewById(R.id.ngaysinhkh_edit)
        dienThoaiKH = findViewById(R.id.dienthoaikh_edit)
        emailKH = findViewById(R.id.emailkh_edit)
        co = findViewById(R.id.co_edit)
        khong = findViewById(R.id.khong_edit)
        btnSuaKH = findViewById(R.id.btnSuaKH)
        toolbarKH = findViewById(R.id.toolbarKH)
        databaseHelper = DatabaseHelper(this)

        setSupportActionBar(toolbarKH)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "KHÁCH HÀNG"
        toolbarKH.setNavigationOnClickListener { finish() }

        val khachHang = intent.getSerializableExtra("khachHang") as KhachHang

        val maKH = khachHang.maKH
        tenKH.setText(khachHang.tenKH)
        diaChiKH.setText(khachHang.diaChi)
        ngaySinhKH.setText(khachHang.ngaySinh)
        dienThoaiKH.setText(khachHang.dienThoai)
        emailKH.setText(khachHang.email)
        if(khachHang.thanhVien == "Có")
        {
            co.isChecked = true
            khong.isChecked = false
        }
        else
        {
            co.isChecked = false
            khong.isChecked = true
        }

        btnSuaKH.setOnClickListener {
            val name = tenKH.text.toString()
            val dc = diaChiKH.text.toString()
            val ns = ngaySinhKH.text.toString()
            val dt = dienThoaiKH.text.toString()
            val email = emailKH.text.toString()
            val tvien = if(co.isChecked == true) "Có" else "Không"
            if(name.isNotEmpty() && dc.isNotEmpty() && ns.isNotEmpty() && dt.isNotEmpty() && tvien.isNotEmpty())
            {
                databaseHelper.updateKH(maKH, name, dc, ns, dt, email, tvien)
                Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show()
                // Gửi broadcast để cập nhật dữ liệu
                val intent = Intent("UPDATE_KH")
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                finish()  // Kết thúc activity sau khi gửi broadcast
            }
            else
            {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
            }
            // quay lại màn hình trước
            finish()
        }

    }
}