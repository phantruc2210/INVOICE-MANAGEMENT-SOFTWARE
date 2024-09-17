package com.example.qlhoadon

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DangKyActivity : AppCompatActivity() {
    // khai báo toàn cục
    private lateinit var maNV : EditText
    private lateinit var hoNV : EditText
    private lateinit var tenNV : EditText
    private lateinit var nam : RadioButton
    private lateinit var nu : RadioButton
    private lateinit var ngaySinh : EditText
    private lateinit var diaChi : EditText
    private lateinit var dienThoai : EditText
    private lateinit var noiSinh : EditText
    private lateinit var ngayVL : EditText
    private lateinit var email : EditText
    private lateinit var nutBack : ImageView

    private lateinit var nutTiep : Button
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dangky_activity)
        // khai báo sd
        maNV = findViewById(R.id.edtNvMa)
        hoNV = findViewById(R.id.edtNvHo)
        tenNV = findViewById(R.id.edtNvTen)
        nam = findViewById(R.id.radNam)
        nu = findViewById(R.id.radNu)
        ngaySinh = findViewById(R.id.edtNvNgaySinh)
        diaChi = findViewById(R.id.edtNvDiaChi)
        dienThoai = findViewById(R.id.edtNvDienThoai)
        noiSinh = findViewById(R.id.edtNvNoiSinh)
        ngayVL = findViewById(R.id.edtNvNgayVL)
        email = findViewById(R.id.edtNvEmail)
        nutTiep = findViewById(R.id.btnTiepTuc)
        nutBack = findViewById(R.id.imgBack)
        val textDangNhap = findViewById<TextView>(R.id.textDN)
        databaseHelper = DatabaseHelper(this)

        // . Quay lại màn hình chính
        nutBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // cài đặt "Quay lại đăng nhập?"
        textDangNhap.setOnClickListener {
            val intent = Intent(this, DangNhapActivity::class.java)
            startActivity(intent)
        }

        nutTiep.setOnClickListener {
            val id = maNV.text.toString()
            val ho = hoNV.text.toString()
            val ten = tenNV.text.toString()
            val gt = if(nam.isChecked == true) "Nam" else "Nữ"
            val ngaysinh = ngaySinh.text.toString()
            val dc = diaChi.text.toString()
            val dt = dienThoai.text.toString()
            val noisinh = noiSinh.text.toString()
            val nvl = ngayVL.text.toString()
            val mail = email.text.toString()

            if(id.isNotEmpty() && ho.isNotEmpty() && ten.isNotEmpty() && gt.isNotEmpty() && ngaysinh.isNotEmpty()
                && dc.isNotEmpty() && dt.isNotEmpty() && noisinh.isNotEmpty() && nvl.isNotEmpty() && mail.isNotEmpty())
            {
                if(databaseHelper.isIDnvExists(id))
                {
                    Toast.makeText(this, "Mã nhân viên đã tồn tại!", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    databaseHelper.addNV(id, ho, ten, gt, ngaysinh, dc, dt, noisinh, nvl, mail)
                    val intent = Intent(this, DangKyTaiKhoanActivity::class.java)
                    this.startActivity(intent)
                }
            }
            else
            {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
            }

        }

    }
}