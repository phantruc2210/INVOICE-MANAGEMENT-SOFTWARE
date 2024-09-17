package com.example.qlhoadon

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class DangNhapActivity : AppCompatActivity() {
    // khai báo toàn cục
    private lateinit var userInputLayout: TextInputLayout
    private lateinit var passInputLayout: TextInputLayout
    private lateinit var checkNhoMK: CheckBox
    private lateinit var textQuenMK: TextView
    private lateinit var nutDangNhap: Button
    private lateinit var textDangKy: TextView
    private lateinit var back : ImageView

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dangnhap_activity)

        // khai báo sử dụng
        userInputLayout = findViewById(R.id.textInputUser)
        passInputLayout = findViewById(R.id.textInputPass)
        checkNhoMK = findViewById(R.id.checkNhoMK)
        textQuenMK = findViewById(R.id.textQuenMK)
        nutDangNhap = findViewById(R.id.btnDangNhap)
        textDangKy = findViewById(R.id.textDangKy)
        back = findViewById(R.id.imgBack)
        databaseHelper = DatabaseHelper(this)

        // 0. Quay lại màn hình chính
        back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 1. Ghi nhớ mật khẩu
        // Thiết lập SharedPreferences
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Tự động điền thông tin đăng nhập nếu người dùng đã chọn ghi nhớ mật khẩu
        val rememberMe = sharedPref.getBoolean("rememberMe", false)
        if (rememberMe) {
            userInputLayout.editText?.setText(sharedPref.getString("username", ""))
            passInputLayout.editText?.setText(sharedPref.getString("password", ""))
            checkNhoMK.isChecked = true
        }


        userInputLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Kiểm tra xem tên đăng nhập có trong SharedPreferences không
                val savedUsername = sharedPref.getString("username", "")
                if (s.toString() == savedUsername) {
                    passInputLayout.editText?.setText(sharedPref.getString("password", ""))
                } else {
                    passInputLayout.editText?.setText("")
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 2. Quên Mật Khẩu
        textQuenMK.setOnClickListener {
            val intent = Intent(this, QuenMKActivity::class.java)
            startActivity(intent)
        }

        // 3. Đăng ký
        textDangKy.setOnClickListener {
            val intent = Intent(this, DangKyActivity::class.java)
            startActivity(intent)
        }

        // 4. Đăng nhập
        nutDangNhap.setOnClickListener {
            val username = userInputLayout.editText?.text.toString().trim()
            val password = passInputLayout.editText?.text.toString().trim()

            var isValid = true

            if (username.isEmpty()) {
                userInputLayout.error = "Vui lòng nhập tên đăng nhập"
                isValid = false
            } else {
                userInputLayout.error = null
            }

            if (password.isEmpty()) {
                passInputLayout.error = "Vui lòng nhập mật khẩu"
                isValid = false
            } else {
                passInputLayout.error = null
            }

            if (!isValid) {
                Toast.makeText(this, "Vui lòng nhập tên đăng nhập và mật khẩu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (databaseHelper.checkLogin(username, password)) {
                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()

                // Lưu thông tin đăng nhập nếu người dùng chọn ghi nhớ mật khẩu
                val editor = sharedPref.edit()
                if (checkNhoMK.isChecked) {
                    editor.putBoolean("rememberMe", true)
                    editor.putString("username", username)
                    editor.putString("password", password)
                } else {
                    editor.putBoolean("rememberMe", false)
                    editor.putString("username", "")
                    editor.putString("password", "")
                }
                editor.apply()

                // Lấy thông tin người dùng từ cơ sở dữ liệu
                val userDetails = databaseHelper.getUserDetails(username)
                val intent = Intent(this, MainHomeActivity::class.java).apply {
                    putExtra("EXTRA_FULLNAME", userDetails?.fullName)
                    putExtra("EXTRA_EMAIL", userDetails?.email)
                    putExtra("EXTRA_IDNV", userDetails?.id)
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
