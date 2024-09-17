package com.example.qlhoadon

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class DangKyTaiKhoanActivity : AppCompatActivity() {
    // khai báo toàn cục
    private lateinit var danhSachNhanvien: List<String>
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var tendnLayout: TextInputLayout
    private lateinit var mkLayout: TextInputLayout
    private lateinit var nhaplaimkLayout: TextInputLayout
    private lateinit var tendn: TextInputEditText
    private lateinit var mk: TextInputEditText
    private lateinit var nhaplaimk: TextInputEditText
    private lateinit var nutDK: Button
    private lateinit var spinNV: Spinner
    private lateinit var textDangNhap: TextView
    private lateinit var nutBack : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dangkytaikhoan_activity)

        // khai báo sd
        tendnLayout = findViewById(R.id.edtTenDNLayout)
        mkLayout = findViewById(R.id.edtMatKhauLayout)
        nhaplaimkLayout = findViewById(R.id.edtNhapLaiMKLayout)
        tendn = findViewById(R.id.edtTenDN)
        mk = findViewById(R.id.edtMatKhau)
        nhaplaimk = findViewById(R.id.edtNhapLaiMK)
        nutDK = findViewById(R.id.btnDangKy)
        spinNV = findViewById(R.id.spinnerNhanVien)
        textDangNhap = findViewById(R.id.textDN)
        nutBack = findViewById(R.id.imgBack)

        databaseHelper = DatabaseHelper(this)
        danhSachNhanvien = databaseHelper.getNameNV()

        // cài đặt adapter
        val adapterNV = ArrayAdapter(this, android.R.layout.simple_spinner_item, danhSachNhanvien)
        adapterNV.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinNV.adapter = adapterNV

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

        // cài đặt "Đăng Ký"
        nutDK.setOnClickListener {
            val user = tendn.text.toString().trim()
            val pass = mk.text.toString().trim()
            val nhappass = nhaplaimk.text.toString().trim()
            val nv = spinNV.selectedItem.toString()

            tendnLayout.error = null
            mkLayout.error = null
            nhaplaimkLayout.error = null

            if (user.isEmpty() || pass.isEmpty() || nhappass.isEmpty()) {
                if (user.isEmpty()) tendnLayout.error = "Vui lòng nhập tên đăng nhập"
                if (pass.isEmpty()) mkLayout.error = "Vui lòng nhập mật khẩu"
                if (nhappass.isEmpty()) nhaplaimkLayout.error = "Vui lòng nhập lại mật khẩu"
                return@setOnClickListener
            }

            if (pass != nhappass) {
                nhaplaimkLayout.error = "Mật khẩu nhập lại không khớp"
                return@setOnClickListener
            }

            if (databaseHelper.isUsernameExists(user)) {
                tendnLayout.error = "Tên đăng nhập đã tồn tại"
                return@setOnClickListener
            }

            databaseHelper.addTK(user, pass, nv)
            Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
            // email đk thành công
            sendRegistrationEmail(nv, user, pass)
            // làm mới
            tendn.text?.clear()
            mk.text?.clear()
            nhaplaimk.text?.clear()
            spinNV.setSelection(0)
        }

    }

    private fun sendRegistrationEmail(nvName: String, username: String, password: String) {
        val email = databaseHelper.getEmailByUsername(username) ?: return

        val fromEmail = "2121005137@sv.ufm.edu.vn"
        val fromPassword = "Oin11469"
        val subject = "Moss Thông báo đăng ký tài khoản thành công"
        val body = """
            Xin chào ${nvName},
            
            Tài khoản của bạn đã được tạo thành công.
            Tên đăng nhập: $username
            Mật khẩu: $password
            
            Nếu cần hỗ trợ thêm, vui lòng liên hệ với chúng tôi 
            qua email: 2121005137@sv.ufm.edu.vn hoặc 2121005303@sv.ufm.edu.vn.
        """.trimIndent()

        Thread {
            EmailSender.sendEmail(fromEmail, fromPassword, email, subject, body)
        }.start()
    }
}
