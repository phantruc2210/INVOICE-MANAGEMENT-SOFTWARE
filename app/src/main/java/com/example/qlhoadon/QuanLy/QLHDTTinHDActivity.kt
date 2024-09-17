package com.example.qlhoadon.QuanLy

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qlhoadon.DatabaseHelper
import com.example.qlhoadon.LopAdapter.CTHoaDonAdapter
import com.example.qlhoadon.LopProduct.HoaDon
import com.example.qlhoadon.R

class QLHDTTinHDActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var toolbar: Toolbar

    private lateinit var textViewSoHD: TextView
    private lateinit var textNgayLap: TextView
    private lateinit var textNV: TextView
    private lateinit var textKH: TextView
    private lateinit var textPTTT: TextView
    private lateinit var textTong: TextView
    private lateinit var textGiam: TextView
    private lateinit var textPhiVC: TextView
    private lateinit var textThanhTien: TextView
    private lateinit var textNhan: TextView
    private lateinit var recyclerViewCTHD: RecyclerView
    private lateinit var chiTietHoaDonAdapter: CTHoaDonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_hoadon)

        // Initialize views
        textViewSoHD = findViewById(R.id.textViewSoHD)
        textNgayLap = findViewById(R.id.textNgayLap)
        textNV = findViewById(R.id.textNV)
        textKH = findViewById(R.id.textKH)
        textPTTT = findViewById(R.id.textPTTT)
        textTong = findViewById(R.id.textTong)
        textGiam = findViewById(R.id.textGiam)
        textPhiVC = findViewById(R.id.textPhiVC)
        textThanhTien = findViewById(R.id.textThanhTien)
        textNhan = findViewById(R.id.textNhan)
        recyclerViewCTHD = findViewById(R.id.recyclerViewCTHD)

        // Initialize database helper
        databaseHelper = DatabaseHelper(this)
        toolbar = findViewById(R.id.toolbarCTHD)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "HÓA ĐƠN"
        toolbar.setNavigationOnClickListener { finish() }

        // Set up RecyclerView
        recyclerViewCTHD.layoutManager = LinearLayoutManager(this)

        // Get invoice details from intent
        val hoaDon = intent.getSerializableExtra("Hoadon") as? HoaDon
        if (hoaDon != null) {
            populateHoaDonDetails(hoaDon)
        } else {
            Toast.makeText(this, "Không thể lấy thông tin hóa đơn", Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateHoaDonDetails(hoaDon: HoaDon) {
        textViewSoHD.text = hoaDon.soHD
        textNgayLap.text = hoaDon.ngayLap
        textPTTT.text = hoaDon.PTTT
        textGiam.text = hoaDon.giamGia.toString()
        textPhiVC.text = hoaDon.phiVC.toString()
        textThanhTien.text = hoaDon.tongHD.toString()
        textNhan.text = hoaDon.tongHD.toString()

        val donHangList = databaseHelper.getDHBySoHD(hoaDon.soHD)
        if (donHangList.isNotEmpty()) {
            val donHang = donHangList[0]
            textNV.text = donHang.NV
            textKH.text = donHang.KH

            val tong = databaseHelper.getTongDH(donHang.maDH)
            textTong.text = tong.toString()

            // thong tin chi tiet hd
            val chiTietHoaDonList = databaseHelper.getCTDHByID(donHang.maDH)

            if (chiTietHoaDonList.isEmpty()) {
                Toast.makeText(this, "Không có chi tiết hóa đơn nào", Toast.LENGTH_SHORT).show()
            } else {
                // lấy danh sach sp
                val danhSachSanPham = databaseHelper.getAllSP()
                // adapter cho RecyclerView
                chiTietHoaDonAdapter = CTHoaDonAdapter(chiTietHoaDonList, danhSachSanPham)
                recyclerViewCTHD.adapter = chiTietHoaDonAdapter
            }
        } else {
            Toast.makeText(this, "Không thể lấy thông tin đơn hàng", Toast.LENGTH_SHORT).show()
        }
    }
}
