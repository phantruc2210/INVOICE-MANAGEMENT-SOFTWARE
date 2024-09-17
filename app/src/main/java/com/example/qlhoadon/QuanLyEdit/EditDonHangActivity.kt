package com.example.qlhoadon.QuanLyEdit

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.qlhoadon.DatabaseHelper
import com.example.qlhoadon.EmailSender
import com.example.qlhoadon.LopProduct.DonHang
import com.example.qlhoadon.LopProduct.KhachHang
import com.example.qlhoadon.QuanLy.QuanLyDonHangView
import com.example.qlhoadon.R

class EditDonHangActivity : AppCompatActivity() {
    // khai báo toàn cục
    private lateinit var ngayDH: EditText
    private lateinit var trangThai: Spinner
    private lateinit var NV: Spinner
    private lateinit var KH: Spinner
    private lateinit var btnSuaDH: Button
    private lateinit var databaseHelper: DatabaseHelper

    private lateinit var toolbarDH : Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_donhang)
        // khai báo sử dụng
        ngayDH = findViewById(R.id.ngaydh_edit)
        trangThai = findViewById(R.id.spin_tthaidh_edit)
        NV = findViewById(R.id.spin_nv_dh_edit)
        KH = findViewById(R.id.spin_kh_dh_edit)
        btnSuaDH = findViewById(R.id.btnSuaDH)
        toolbarDH = findViewById(R.id.toolbarDH)
        databaseHelper = DatabaseHelper(this)

        setSupportActionBar(toolbarDH)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "ĐƠN HÀNG"
        toolbarDH.setNavigationOnClickListener { finish() }

        // Dữ liệu mẫu cho Spinner
        val danhSachTrangThai = listOf("Đặt hàng thành công", "Đã giao hàng", "Giao hàng thành công")
        val danhSachNV = databaseHelper.getAllNV()
        val danhSachKH = databaseHelper.getAllKH()


        // Thiết lập Adapter cho Spinner
        val adapterTT = ArrayAdapter(this, android.R.layout.simple_spinner_item, danhSachTrangThai)
        adapterTT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        trangThai.adapter = adapterTT

        val danhSachNVNames = danhSachNV.map { it.hoNV + " " + it.tenNV }
        val adapterNV = ArrayAdapter(this, android.R.layout.simple_spinner_item, danhSachNVNames)
        adapterNV.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        NV.adapter = adapterNV

        val danhSachKHNames = danhSachKH.map { it.tenKH }
        val adapterKH = ArrayAdapter(this, android.R.layout.simple_spinner_item, danhSachKHNames)
        adapterKH.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        KH.adapter = adapterKH
        // Nhận dữ liệu từ Intent
        val donHang = intent.getSerializableExtra("DonHang") as DonHang
        val maDH = donHang.maDH
        ngayDH.setText(donHang.ngayDH)

        // Thiết lập giá trị ban đầu cho Spinner
        trangThai.setSelection(danhSachTrangThai.indexOf(donHang.trangThai))
        NV.setSelection(danhSachNV.indexOfFirst { it.hoNV + " " + it.tenNV == donHang.NV })
        KH.setSelection(danhSachKH.indexOfFirst { it.tenKH == donHang.KH })

        btnSuaDH.setOnClickListener {
            // Lấy dữ liệu mới từ các trường nhập liệu và Spinner
            val ngaydh = ngayDH.text.toString()
            val tthai = trangThai.selectedItem.toString()
            val nv = NV.selectedItem.toString()
            val kh = KH.selectedItem.toString()
            if(ngaydh.isNotEmpty() && tthai.isNotEmpty() && nv.isNotEmpty() && kh.isNotEmpty())
            {
                databaseHelper.updateDH(maDH, ngaydh, tthai, nv, kh)
                Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show()

                // Gửi email thông báo dựa trên trạng thái mới
                val donHang = DonHang(maDH, ngaydh, tthai, nv, kh) // Tạo đối tượng DonHang từ dữ liệu nhập vào
                QuanLyDonHangView(this).sendEmailBasedOnStatus(donHang, tthai) // Gọi phương thức gửi email

                // Gửi broadcast để cập nhật dữ liệu
                val intent = Intent("UPDATE_DH")
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