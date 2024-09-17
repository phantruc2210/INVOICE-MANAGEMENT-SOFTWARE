package com.example.qlhoadon

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class QuenMKActivity : AppCompatActivity() {
    // Khai báo toàn cục
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var btnGui: Button
    private lateinit var btnCancel: Button
    private lateinit var edtUsername: TextInputEditText
    private lateinit var usernameLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quen_mk_activity)

        // Khai báo sử dụng
        databaseHelper = DatabaseHelper(this)
        edtUsername = findViewById(R.id.edtUsername)
        btnGui = findViewById(R.id.btnGui)
        btnCancel = findViewById(R.id.btnHuy)
        usernameLayout = findViewById(R.id.usernameLayout)

        // Xử lý khi nhấn nút "Gửi"
        btnGui.setOnClickListener {
            val username = edtUsername.text.toString().trim()
            if (username.isEmpty()) {
                usernameLayout.error = "Vui lòng nhập tên đăng nhập" // Hiển thị cảnh báo lỗi
            } else {
                usernameLayout.error = null
                val email = databaseHelper.getEmailByUsername(username)
                if (email != null) {
                    sendPasswordResetEmail(email)
                    usernameLayout.error = null
                    Toast.makeText(this, "Email lấy lại MK đã được gửi đến email $email " +
                            " của bạn!", Toast.LENGTH_SHORT).show()
                } else {
                    usernameLayout.error = "Tên đăng nhập không tồn tại" // Hiển thị cảnh báo lỗi
                }
            }
        }

        // Xử lý khi nhấn nút "Hủy"
        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        val fromEmail = "2121005137@sv.ufm.edu.vn"
        val fromPassword = "Oin11469"
        val subject = "Moss Thông báo mật khẩu mới lấy lại mật khẩu "
        val newPassword = generateRandomPassword(8)
        val body = "Xin chào, mật khẩu mới của bạn là: $newPassword. " +
                "Không chia sẻ mật khẩu mới với bất cứ ai, bạn có thể truy cập và đổi mật khẩu sau đó !"
        // Update MK Mới
        databaseHelper.updateUserPassword(email, newPassword)
        Thread {
            EmailSender.sendEmail(fromEmail, fromPassword, email, subject, body)
        }.start()
    }

    fun generateRandomPassword(length: Int): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_+=<>?"
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}
