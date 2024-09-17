package com.example.qlhoadon.QuanLy

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.qlhoadon.DatabaseHelper
import com.example.qlhoadon.LopProduct.DonHang
import com.example.qlhoadon.R

class TaoHDActivity : AppCompatActivity() {
    // khai báo toàn cục
    private lateinit var sohd: EditText
    private lateinit var ngaylap: EditText
    private lateinit var spinnerPTTT: Spinner
    private lateinit var madh: EditText
    private lateinit var tonghd: EditText
    private lateinit var giamgia: EditText
    private lateinit var phivc : EditText
    private lateinit var tongtt: EditText
    private lateinit var nutThem: Button
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var danhSachPTTT: List<String>
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_hoadon)
        // khai báo sd
        sohd = findViewById(R.id.sohd)
        ngaylap = findViewById(R.id.ngaylap)
        spinnerPTTT = findViewById(R.id.spinner_pttt_add)
        madh = findViewById(R.id.madh_hd)
        tonghd = findViewById(R.id.tonghd)
        giamgia = findViewById(R.id.giamgia)
        phivc = findViewById(R.id.phivc)
        tongtt = findViewById(R.id.tongthanhtoan)
        nutThem = findViewById(R.id.btnThem)
        databaseHelper = DatabaseHelper(this)

        toolbar = findViewById(R.id.toolbarHD)
        // cài đặt toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "HÓA ĐƠN"
        toolbar.setNavigationOnClickListener { finish() }

        danhSachPTTT = listOf("Chuyển khoản", "Tiền mặt")
        val adapterPTTT = ArrayAdapter(this, android.R.layout.simple_spinner_item, danhSachPTTT)
        adapterPTTT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPTTT.adapter = adapterPTTT

        // Lấy dữ liệu từ intent
        val donHang = intent.getSerializableExtra("DonHang") as? DonHang
        // Kiểm tra nếu donHang là null
        if (donHang == null) {
            Toast.makeText(this, "Không tìm thấy dữ liệu đơn hàng", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        // cài đặt thông tin
        madh.setText(donHang.maDH)
        madh.isEnabled = false // khóa không cho thay đổi
        // Thiết lập số hóa đơn tự động
        val soHoaDonMoi = taoSoHoaDonMoi()
        sohd.setText(soHoaDonMoi)
        sohd.isEnabled = false

        // Thiết lập ngày lập hóa đơn dựa trên ngày hiện tại
        val ngayHienTai = layNgayHienTai()
        ngaylap.setText(ngayHienTai)
        ngaylap.isEnabled = false

        // Tính tổng hóa đơn khi mã đơn hàng thay đổi
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

        nutThem.setOnClickListener {
            val id = sohd.text.toString()
            val ngay = ngaylap.text.toString()
            val pttt = spinnerPTTT.selectedItem.toString()
            val giamGia = giamgia.text.toString().toDoubleOrNull()
            val phiVC = phivc.text.toString().toDoubleOrNull()
            val tongThanhToan = tongtt.text.toString().toDoubleOrNull()

            if (id.isNotEmpty() && ngay.isNotEmpty() && pttt.isNotEmpty() &&
                giamGia != null && phiVC != null && tongThanhToan != null)
            {
                databaseHelper.addHD(id, ngay, pttt, phiVC, giamGia, tongThanhToan, donHang.maDH)
                Toast.makeText(this, "Thêm thành công!", Toast.LENGTH_SHORT).show()
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

    private fun layNgayHienTai(): String {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }

    private fun taoSoHoaDonMoi(): String {
        var soHD: String
        val random = java.util.Random()
        do {
            soHD = "HD${random.nextInt(1000000)}" // Tạo số hóa đơn ngẫu nhiên
        } while (databaseHelper.checkIfSoHDExists(soHD)) // Kiểm tra số hóa đơn đã tồn tại hay chưa
        return soHD
    }
}
