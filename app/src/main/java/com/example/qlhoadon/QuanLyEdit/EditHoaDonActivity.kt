package com.example.qlhoadon.QuanLyEdit

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.qlhoadon.DatabaseHelper
import com.example.qlhoadon.LopProduct.HoaDon
import com.example.qlhoadon.R

class EditHoaDonActivity : AppCompatActivity() {
    // khai báo toàn cục
    private lateinit var sohd: TextView
    private lateinit var ngaylap: TextView
    private lateinit var spinnerPTTT: Spinner
    private lateinit var madh: TextView
    private lateinit var tonghd: EditText
    private lateinit var giamgia: EditText
    private lateinit var phivc : EditText
    private lateinit var tongtt: EditText
    private lateinit var nutSua: Button

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var danhSachPTTT: List<String>
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_hoadon)

        // khai báo sử dụng
        sohd = findViewById(R.id.sohd_edit)
        ngaylap = findViewById(R.id.ngaylap_edit)
        spinnerPTTT = findViewById(R.id.spin_pttt_edit)
        madh = findViewById(R.id.madh_hd_edit)
        tonghd = findViewById(R.id.tonghd_edit)
        giamgia = findViewById(R.id.giamgia_edit)
        phivc = findViewById(R.id.phivc_edit)
        tongtt = findViewById(R.id.tongtt_edit)
        nutSua = findViewById(R.id.btnSuaHD)

        databaseHelper = DatabaseHelper(this)

        danhSachPTTT = listOf("Chuyển khoản", "Tiền mặt")
        val adapterPTTT = ArrayAdapter(this, android.R.layout.simple_spinner_item, danhSachPTTT)
        adapterPTTT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPTTT.adapter = adapterPTTT

        toolbar = findViewById(R.id.toolbarEditHD)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "HÓA ĐƠN"
        toolbar.setNavigationOnClickListener { finish() }

        val hoaDon = intent.getSerializableExtra("Hoadon") as? HoaDon

        if (hoaDon == null) {
            Toast.makeText(this, "Không tìm thấy dữ liệu hóa đơn", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        // cài đặt thông tin
        sohd.setText(hoaDon.soHD)
        sohd.isEnabled = false
        ngaylap.setText(hoaDon.ngayLap)
        ngaylap.isEnabled = false
        spinnerPTTT.setSelection(danhSachPTTT.indexOf(hoaDon.PTTT))

        phivc.setText(hoaDon.phiVC.toString())
        giamgia.setText(hoaDon.giamGia.toString())
        tongtt.setText(hoaDon.tongHD.toString())

        madh.setText(hoaDon.maDH)
        madh.isEnabled = false

        val donHangList = databaseHelper.getDHBySoHD(hoaDon.soHD)
        if (donHangList.isNotEmpty()) {
            val donHang = donHangList[0]

            // tổng hđ dựa trên mã dh
            val tongHD = databaseHelper.getTongDH(donHang.maDH)
            tonghd.setText(tongHD.toString())

            /// tính tổng thanh toán khi nhập giảm giá và phí vận chuyển
            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    tinhTongThanhToan(tongHD)
                }
            }
            giamgia.addTextChangedListener(textWatcher)
            phivc.addTextChangedListener(textWatcher)

        } else {
            Toast.makeText(this, "Không thể lấy thông tin đơn hàng", Toast.LENGTH_SHORT).show()
        }

        // sửa hóa đơn
        nutSua.setOnClickListener {
            val id = sohd.text.toString()
            val ngay = ngaylap.text.toString()
            val pttt = spinnerPTTT.selectedItem.toString()
            val giamGia = giamgia.text.toString().toDoubleOrNull()
            val phiVC = phivc.text.toString().toDoubleOrNull()
            val tongThanhToan = tongtt.text.toString().toDoubleOrNull()
            val iddh = madh.text.toString()

            if ( pttt.isNotEmpty() &&
                giamGia != null && phiVC != null && tongThanhToan != null)
            {
                databaseHelper.updateHD(id, ngay, pttt, phiVC, giamGia, tongThanhToan, iddh)
                Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show()
                // Gửi broadcast để cập nhật dữ liệu
                val intent = Intent("UPDATE_HD")
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
    private fun tinhTongThanhToan(tongHD: Double) {
        val giamGiaText = giamgia.text.toString()
        val phiVCText = phivc.text.toString()

        val giamGia = if (giamGiaText.isNotEmpty()) giamGiaText.toDouble() else 0.0
        val phiVC = if (phiVCText.isNotEmpty()) phiVCText.toDouble() else 0.0

        val tongThanhToan = tongHD - giamGia + phiVC
        tongtt.setText(tongThanhToan.toString())
    }


}
