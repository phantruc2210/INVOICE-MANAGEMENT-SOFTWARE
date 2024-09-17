package com.example.qlhoadon.QuanLy

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import com.example.qlhoadon.DangNhapActivity
import com.example.qlhoadon.DatabaseHelper
import com.example.qlhoadon.DoiMKThanhCongActivity
import com.example.qlhoadon.EmailSender
import com.example.qlhoadon.MainActivity
import com.example.qlhoadon.R

class NguoiDungTKView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var tendn : EditText
    private lateinit var mk_hientai : EditText
    private lateinit var mk_moi : EditText
    private lateinit var mk_xacnhan : EditText
    private lateinit var nutDoiMK : Button
    private lateinit var nutXoaTK : Button

    init {
        LayoutInflater.from(context).inflate(R.layout.view_taikhoan, this, true)
        // khai báo sd
        databaseHelper = DatabaseHelper(context)
        tendn = findViewById(R.id.edtTK_TenDN)
        mk_hientai = findViewById(R.id.edtTK_MKHT)
        mk_moi = findViewById(R.id.edtTK_MKMoi)
        mk_xacnhan = findViewById(R.id.edtTK_NhapMKLai)
        nutDoiMK = findViewById(R.id.btnDoiMK)
        nutXoaTK = findViewById(R.id.btnXoaTK)

        nutDoiMK.setOnClickListener {
            doiMatKhau()
        }

        nutXoaTK.setOnClickListener {
            xoaTaiKhoan()
        }
    }

    fun setTKId(employeeId: String) {
        showDL(employeeId)
    }

    private fun showDL(employeeId: String) {
        val tkList = databaseHelper.getTKByID(employeeId)
        if (tkList.isNotEmpty()) {
            val tk = tkList[0]
            tendn.setText(tk.tenDN)
            mk_hientai.setText(tk.matKhau)
        }
    }

    private fun doiMatKhau() {
        val tenDN = tendn.text.toString().trim()
        val mkHienTai = mk_hientai.text.toString().trim()
        val mkMoi = mk_moi.text.toString().trim()
        val mkXacNhan = mk_xacnhan.text.toString().trim()

        if (tenDN.isEmpty() || mkHienTai.isEmpty() || mkMoi.isEmpty() || mkXacNhan.isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        if (mkMoi != mkXacNhan) {
            Toast.makeText(context, "Mật khẩu mới không trùng khớp", Toast.LENGTH_SHORT).show()
            return
        }

        val currentTK = databaseHelper.getTKByTenDN(tenDN)
        if (currentTK.isEmpty() || currentTK[0].matKhau != mkHienTai) {
            Toast.makeText(context, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show()
            return
        }

        val isUpdated = databaseHelper.updateTK(tenDN, mkMoi)
        if (isUpdated) {
            Toast.makeText(context, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
            // Gửi thông báo qua email
            val fromEmail = "2121005137@sv.ufm.edu.vn"
            val fromPassword = "Oin11469"
            val toEmail = databaseHelper.getEmailByUsername(tenDN)
            if (toEmail != null) {
                val subject = "Moss Thông báo đổi mật khẩu"
                val body = "Tài khoản của bạn ($tenDN) đã đổi mật khẩu thành công. Mật khẩu mới của bạn là: $mkMoi"
                Thread {
                    EmailSender.sendEmail(fromEmail, fromPassword, toEmail, subject, body)
                }.start()

                // Truy cập đổi mật khẩu thành công
                val intent = Intent(context, DoiMKThanhCongActivity::class.java)
                context.startActivity(intent)
            }
            else
            {
            Toast.makeText(context, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun xoaTaiKhoan() {
        val tenDN = tendn.text.toString().trim()

        if (tenDN.isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập tên đăng nhập", Toast.LENGTH_SHORT).show()
            return
        }

        // Lấy thông tin tài khoản
        val currentTK = databaseHelper.getTKByTenDN(tenDN)
        if (currentTK.isEmpty()) {
            Toast.makeText(context, "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show()
            return
        }

        // Xóa tài khoản
        val isDeletedTK = databaseHelper.deleteTK(tenDN)
        if (isDeletedTK) {
            // Xóa nhân viên liên quan
            val name = currentTK[0].nhanVien
            val isDeletedNV = databaseHelper.deleteNV(name)
            if (isDeletedNV) {
                Toast.makeText(context, "Xóa tài khoản và nhân viên thành công", Toast.LENGTH_SHORT).show()

                // Đăng xuất và quay về màn hình ban đầu
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Xóa tài khoản thành công nhưng xóa nhân viên thất bại", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Xóa tài khoản thất bại", Toast.LENGTH_SHORT).show()
        }
    }

}