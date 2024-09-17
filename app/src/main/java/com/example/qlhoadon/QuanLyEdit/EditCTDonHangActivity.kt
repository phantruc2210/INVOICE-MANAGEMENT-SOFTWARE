package com.example.qlhoadon.QuanLyEdit

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.qlhoadon.DatabaseHelper
import com.example.qlhoadon.LopProduct.CTDonHang
import com.example.qlhoadon.R

class EditCTDonHangActivity : AppCompatActivity() {
    private lateinit var soLuong: EditText
    private lateinit var SP: Spinner
    private lateinit var donGia: EditText
    private lateinit var thanhTien: EditText
    private lateinit var toolbarCTDH : Toolbar
    private lateinit var databaseHelper: DatabaseHelper

    private lateinit var btnSuaCTDH: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_ctdonhang)

        soLuong = findViewById(R.id.soluong_ctdh_edit)
        SP = findViewById(R.id.spin_sp_ctdh_edit)
        donGia = findViewById(R.id.giaban_ctdh_edit)
        thanhTien = findViewById(R.id.thanhtien_ctdh_edit)
        toolbarCTDH = findViewById(R.id.toolbarCTDH)
        databaseHelper = DatabaseHelper(this)

        btnSuaCTDH = findViewById(R.id.btnSuaCTDH)

        setSupportActionBar(toolbarCTDH)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "CHI TIẾT ĐƠN HÀNG"
        toolbarCTDH.setNavigationOnClickListener { finish() }

        val danhSachSP = databaseHelper.getAllSP()
        val danhsachSPNames = danhSachSP.map { it.tenSP }
        val adapterSP = ArrayAdapter(this, android.R.layout.simple_spinner_item, danhsachSPNames)
        adapterSP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        SP.adapter = adapterSP

        val CTdonHang = intent.getSerializableExtra("CTDonHang") as CTDonHang
        soLuong.setText(CTdonHang.soLuong.toString())
        donGia.setText(CTdonHang.giaBan.toString())
        thanhTien.setText(CTdonHang.thanhTien.toString())
        SP.setSelection(danhSachSP.indexOfFirst { it.tenSP == CTdonHang.SP })

        // Chặn người dùng tương tác với Spinner
        SP.setOnTouchListener { _, _ -> true }

        // Tự động điền thông tin giá bán khi chọn sản phẩm
        SP.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedSP = danhSachSP[position]
                donGia.setText(selectedSP.giaBan.toString())
                // Tính toán thành tiền nếu số lượng đã được nhập
                val quantity = soLuong.text.toString().toIntOrNull()
                if (quantity != null) {
                    thanhTien.setText((quantity * selectedSP.giaBan).toString())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Tính toán thành tiền khi số lượng thay đổi
        soLuong.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val quantity = s.toString().toIntOrNull()
                val price = donGia.text.toString().toDoubleOrNull()
                if (quantity != null && price != null) {
                    thanhTien.setText((quantity * price).toString())
                } else {
                    thanhTien.setText("")
                }
            }
        })

        btnSuaCTDH.setOnClickListener {
            val sp = SP.selectedItem.toString()
            val sl = soLuong.text.toString().toIntOrNull()
            val gia = donGia.text.toString().toDoubleOrNull()
            val tien = thanhTien.text.toString().toDoubleOrNull()

            if (sl != null && gia != null && tien != null && sp.isNotEmpty()) {
                if(sl <= 0)
                {
                    Toast.makeText(this, "Số lượng phải lớn hơn 0!", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    databaseHelper.updateCTDH(CTdonHang.maDH, sp, sl, gia, tien)
                    Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show()

                    // Gửi broadcast để cập nhật dữ liệu
                    val intent = Intent("UPDATE_CTDH")
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

                    // Debug log để xác nhận việc gửi broadcast
                    Log.d("EditCTDonHangActivity", "Broadcast UPDATE_CTDH sent.")

                    finish()  // Kết thúc activity sau khi gửi broadcast
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
            }
        }


    }
}
