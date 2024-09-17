package com.example.qlhoadon.QuanLy

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qlhoadon.DatabaseHelper
import com.example.qlhoadon.LopAdapter.CTDonHangAdapter
import com.example.qlhoadon.LopProduct.CTDonHang
import com.example.qlhoadon.LopProduct.DonHang
import com.example.qlhoadon.LopProduct.SanPham
import com.example.qlhoadon.QuanLyEdit.EditCTDonHangActivity
import com.example.qlhoadon.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class QLDHChiTietDHActivity : AppCompatActivity() {

    private lateinit var recyclerViewCTDH: RecyclerView
    private lateinit var adapterCTDH: CTDonHangAdapter
    private lateinit var textViewMaDH: TextView
    private lateinit var toolbar : Toolbar
    private lateinit var danhSachSP : List<SanPham>
    private lateinit var databaseHelper: DatabaseHelper

    // xử lý update trong Broadcast
    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateCTDHList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_ctdonhang)

        // Lấy dữ liệu từ intent
        val donHang = intent.getSerializableExtra("DonHang") as? DonHang
        textViewMaDH = findViewById(R.id.textViewMaDH)

        // Kiểm tra nếu donHang là null
        if (donHang == null) {
            Toast.makeText(this, "Không tìm thấy dữ liệu đơn hàng", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        textViewMaDH.text = donHang.maDH
        recyclerViewCTDH = findViewById(R.id.recyclerViewCTDH)
        recyclerViewCTDH.layoutManager = LinearLayoutManager(this)

        databaseHelper = DatabaseHelper(this)
        danhSachSP = databaseHelper.getAllSP()

        toolbar = findViewById(R.id.toolbarViewCTDH)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "CHI TIẾT ĐƠN HÀNG"
        toolbar.setNavigationOnClickListener { finish() }

        // Khởi tạo adapter với danh sách trống ban đầu
        adapterCTDH = CTDonHangAdapter(
            emptyList(),
            danhSachSP,
            onEditClick = { ctdh ->
                val intent = Intent(this, EditCTDonHangActivity::class.java)
                intent.putExtra("CTDonHang", ctdh)
                this.startActivity(intent)
            },
            onDeleteClick = { ctdh ->
                showDeleteConfirmationDialog(ctdh)
            }
        )
        recyclerViewCTDH.adapter = adapterCTDH

        val floatingActionButtonThem: FloatingActionButton = findViewById(R.id.floatButtonCTDH)
        floatingActionButtonThem.setOnClickListener {
            showAddCTDonHangDialog()
        }

        // Cập nhật danh sách chi tiết đơn hàng sau khi adapter đã được khởi tạo
        updateCTDHList()
    }

    // thêm chi tiết đơn hàng
    private fun showAddCTDonHangDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogLayout = inflater.inflate(R.layout.add_ctdonhang, null)

        val madh = dialogLayout.findViewById<EditText>(R.id.madh_ctdh)
        val sp = dialogLayout.findViewById<Spinner>(R.id.spinner_sp_ctdh)
        val sl = dialogLayout.findViewById<EditText>(R.id.soluong_ctdh)
        val gia = dialogLayout.findViewById<EditText>(R.id.giaban_ctdh)
        val tong = dialogLayout.findViewById<EditText>(R.id.thanhtien_ctdh)

        // Thiết lập mã đơn hàng tự động
        val currentMaDH = textViewMaDH.text.toString()
        madh.setText(currentMaDH)
        madh.isEnabled = false // Khóa EditText để người dùng không thay đổi mã đơn hàng

        // Thiết lập Adapter cho Spinner sản phẩm
        val danhSachSPNames = danhSachSP.map { it.tenSP }
        val adapterSP = ArrayAdapter(this, android.R.layout.simple_spinner_item, danhSachSPNames)
        adapterSP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp.adapter = adapterSP

        // Tự động điền thông tin giá bán khi chọn sản phẩm
        sp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedSP = danhSachSP[position]
                gia.setText(selectedSP.giaBan.toString())
                // Tính toán thành tiền nếu số lượng đã được nhập
                val quantity = sl.text.toString().toIntOrNull()
                if (quantity != null) {
                    tong.setText((quantity * selectedSP.giaBan).toString())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Tính toán thành tiền khi số lượng thay đổi
        sl.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val quantity = s.toString().toIntOrNull()
                val price = gia.text.toString().toDoubleOrNull()
                if (quantity != null && price != null) {
                    tong.setText((quantity * price).toString())
                } else {
                    tong.setText("")
                }
            }
        })

        builder.setView(dialogLayout)
        builder.setTitle("Thêm Chi Tiết Đơn Hàng")
        builder.setPositiveButton("THÊM") { dialogInterface, _ ->
            val maDH = madh.text.toString()
            val sanPham = sp.selectedItem.toString()
            val soLuong = sl.text.toString().toIntOrNull()
            val giaBan = gia.text.toString().toDoubleOrNull()
            val thanhTien = tong.text.toString().toDoubleOrNull()

            if (maDH.isNotEmpty() && sanPham.isNotEmpty() && soLuong != null && giaBan != null && thanhTien != null) {
                databaseHelper.addCTDH(maDH, sanPham, soLuong, giaBan, thanhTien)
                if(soLuong <= 0)
                {
                    Toast.makeText(this, "Số lượng phải lớn hơn 0!", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(this, "Thêm thành công!", Toast.LENGTH_SHORT).show()
                    // Xóa các ô sau khi thêm thành công
                    sl.text.clear()
                    gia.text.clear()
                    tong.text.clear()
                    sp.setSelection(0)
                    updateCTDHList()
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("THOÁT") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        builder.show()
    }

    // xóa
    private fun showDeleteConfirmationDialog(CTDonHang: CTDonHang) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa chi tiết đơn hàng này?")
            .setPositiveButton("XÓA") { dialog, _ ->
                databaseHelper.deleteCTDH(CTDonHang.maDH, CTDonHang.SP)
                Toast.makeText(this, "Xóa thành công!", Toast.LENGTH_SHORT).show()
                updateCTDHList()
            }
            .setNegativeButton("HỦY") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun getCTDHList(maDH: String): List<CTDonHang> {
        return databaseHelper.getCTDHByID(maDH)
    }

    private fun updateCTDHList() {
        val donHangID = textViewMaDH.text.toString()
        val chiTietDonHangList = getCTDHList(donHangID)
        if (chiTietDonHangList.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm trong đơn hàng", Toast.LENGTH_SHORT).show()
        }
        adapterCTDH.updateList(chiTietDonHangList)
    }
    // xử lý broadcast
    private fun registerReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(updateReceiver, IntentFilter("UPDATE_CTDH"))
    }

    private fun unregisterReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        registerReceiver()
    }

    override fun onDetachedFromWindow() {
        unregisterReceiver()
        super.onDetachedFromWindow()
    }

}
