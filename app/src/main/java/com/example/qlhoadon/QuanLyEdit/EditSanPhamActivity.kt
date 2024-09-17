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
import com.example.qlhoadon.LopProduct.SanPham
import com.example.qlhoadon.R

class EditSanPhamActivity : AppCompatActivity() {
    // khai báo toàn cục
    private lateinit var tenSP: EditText
    private lateinit var slTon: EditText
    private lateinit var giaBan: EditText
    private lateinit var donvitinh: Spinner
    private lateinit var LSP: Spinner
    private lateinit var anh:EditText
    private lateinit var btnSuaSP: Button

    private lateinit var toolbarSP: Toolbar
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_sanpham)
        // khai báo sử dụng
        tenSP = findViewById(R.id.tensp_edit)
        slTon = findViewById(R.id.slton_edit)
        giaBan = findViewById(R.id.giaban_edit)
        donvitinh = findViewById(R.id.spin_dvt_edit)
        LSP = findViewById(R.id.spin_lsp_sp_edit)
        anh = findViewById(R.id.linkanh_edit)
        btnSuaSP = findViewById(R.id.btnSuaSP)
        toolbarSP = findViewById(R.id.toolbarSP)
        databaseHelper = DatabaseHelper(this)

        setSupportActionBar(toolbarSP)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "SẢN PHẨM"
        toolbarSP.setNavigationOnClickListener { finish() }

        // Lấy dữ liệu từ cơ sở dữ liệu
        val danhSachDonViTinh = listOf("Cái", "Cặp", "Bộ", "Hộp", "Quyển", "Cây")
        val danhSachLoaiSP = databaseHelper.getAllLSP()

        // Thiết lập Adapter cho Spinner
        val adapterDVT = ArrayAdapter(this, android.R.layout.simple_spinner_item, danhSachDonViTinh)
        adapterDVT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        donvitinh.adapter = adapterDVT

        val loaiSPNames = danhSachLoaiSP.map { it.tenloaiSP }
        val adapterMaLSP = ArrayAdapter(this, android.R.layout.simple_spinner_item, loaiSPNames)
        adapterMaLSP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        LSP.adapter = adapterMaLSP

        val sanPham = intent.getSerializableExtra("sanPham") as SanPham

        val maSP = sanPham.maSP
        tenSP.setText(sanPham.tenSP)
        slTon.setText(sanPham.slTon.toString())
        giaBan.setText(sanPham.giaBan.toString())
        anh.setText(sanPham.anhSP)

        // Thiết lập giá trị ban đầu cho Spinner
        donvitinh.setSelection(danhSachDonViTinh.indexOf(sanPham.donViTinh))
        LSP.setSelection(danhSachLoaiSP.indexOfFirst { it.tenloaiSP == sanPham.loaiSP })

        btnSuaSP.setOnClickListener {
            val name = tenSP.text.toString()
            val sl = slTon.text.toString().toIntOrNull()
            val gia = giaBan.text.toString().toDoubleOrNull()
            val dvt = donvitinh.selectedItem.toString()
            val namelsp = LSP.selectedItem.toString()
            val anhsp = anh.text.toString()
            if(name.isNotEmpty() && sl != null && gia != null && dvt.isNotEmpty() && namelsp.isNotEmpty() && anhsp.isNotEmpty())
            {
                if(sl < 0)
                {
                    Toast.makeText(this, "Số lượng tồn phải lớn hơn bằng 0!", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    databaseHelper.updateSP(maSP, name, sl, gia, dvt, namelsp, anhsp)
                    Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show()
                    // Gửi broadcast để cập nhật dữ liệu
                    val intent = Intent("UPDATE_SP")
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                    finish()  // Kết thúc activity sau khi gửi broadcast
                }
            }
            else
            {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
            }
            finish()
        }

    }
}
