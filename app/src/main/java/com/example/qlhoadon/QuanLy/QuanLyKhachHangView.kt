package com.example.qlhoadon.QuanLy

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.SearchView
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qlhoadon.DatabaseHelper
import com.example.qlhoadon.LopAdapter.KhachHangAdapter
import com.example.qlhoadon.LopProduct.KhachHang
import com.example.qlhoadon.QuanLyEdit.EditKhachHangActivity
import com.example.qlhoadon.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class QuanLyKhachHangView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: KhachHangAdapter
    private lateinit var searchView: SearchView
    private lateinit var databaseHelper: DatabaseHelper

    // xử lý update trong Broadcast
    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateKHList()
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_khachhang, this, true)
        databaseHelper = DatabaseHelper(context)
        setupView()
    }

    private fun setupView()
    {
        recyclerView = findViewById(R.id.recyclerViewKH)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = KhachHangAdapter(
            getKHList(),
            // sửa khách hàng
            onEditClick = { khachHang ->
                val intent = Intent(context, EditKhachHangActivity::class.java)
                intent.putExtra("khachHang", khachHang)
                context.startActivity(intent)
            },
            // xóa khách hàng
            onDeleteClick = { khachHang ->
                showDeleteConfirmationDialog(khachHang)
            }
        )
        recyclerView.adapter = adapter

        // thêm khách hàng
        val floatingActionButton: FloatingActionButton = findViewById(R.id.floatButtonKH)
        floatingActionButton.setOnClickListener {
            showAddKhachHangDialog()
        }
        // Thiết lập tìm kiếm
        searchView = findViewById(R.id.searchViewKH)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true
            }
        })
    }

    // xử lý broadcast
    private fun registerReceiver() {
        LocalBroadcastManager.getInstance(context).registerReceiver(updateReceiver, IntentFilter("UPDATE_KH"))
    }

    private fun unregisterReceiver() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(updateReceiver)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        registerReceiver()
    }

    override fun onDetachedFromWindow() {
        unregisterReceiver()
        super.onDetachedFromWindow()
    }

    private fun showAddKhachHangDialog() {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(R.layout.add_khachhang, null)

        val maKH = dialogLayout.findViewById<EditText>(R.id.makh)
        val tenKH = dialogLayout.findViewById<EditText>(R.id.tenkh)
        val diaChiKH = dialogLayout.findViewById<EditText>(R.id.diachikh)
        val ngaySinhKH = dialogLayout.findViewById<EditText>(R.id.ngaysinhkh)
        val dienThoaiKH = dialogLayout.findViewById<EditText>(R.id.dienthoaikh)
        val emailKH = dialogLayout.findViewById<EditText>(R.id.emailkh)
        val co = dialogLayout.findViewById<RadioButton>(R.id.co)
        val khong = dialogLayout.findViewById<RadioButton>(R.id.khong)

        // Thay đổi thông tin khi mã khách hàng thay đổi
        maKH.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val id = s.toString()
                if (id.isNotEmpty()) {
                    val khachHangList = databaseHelper.getKHByID(id)
                    if (khachHangList.isNotEmpty()) {
                        val khachHang = khachHangList[0]
                        tenKH.setText(khachHang.tenKH)
                        diaChiKH.setText(khachHang.diaChi)
                        ngaySinhKH.setText(khachHang.ngaySinh)
                        dienThoaiKH.setText(khachHang.dienThoai)
                        emailKH.setText(khachHang.email)
                        if (khachHang.thanhVien == "Có") co.isChecked = true else khong.isChecked = true
                    } else {
                        tenKH.setText("")
                        diaChiKH.setText("")
                        ngaySinhKH.setText("")
                        dienThoaiKH.setText("")
                        emailKH.setText("")
                        co.isChecked = false
                        khong.isChecked = false
                    }
                }
            }
        })

        builder.setView(dialogLayout)
        builder.setTitle("Thêm Khách Hàng")
        builder.setPositiveButton("THÊM") { dialogInterface, _ ->
            val id = maKH.text.toString()
            val name = tenKH.text.toString()
            val dc = diaChiKH.text.toString()
            val ns = ngaySinhKH.text.toString()
            val dt = dienThoaiKH.text.toString()
            val email = emailKH.text.toString()
            val tvien = if (co.isChecked) "Có" else "Không"
            if (id.isNotEmpty() && name.isNotEmpty() && dc.isNotEmpty() && ns.isNotEmpty() && dt.isNotEmpty()) {
                databaseHelper.addKH(id, name, dc, ns, dt, email, tvien)
                Toast.makeText(context, "Thêm thành công!", Toast.LENGTH_SHORT).show()
                // xóa các ô
                maKH.text.clear()
                tenKH.text.clear()
                diaChiKH.text.clear()
                ngaySinhKH.text.clear()
                dienThoaiKH.text.clear()
                emailKH.text.clear()
                co.isChecked = false
                khong.isChecked = false
                updateKHList()

            } else {
                Toast.makeText(context, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("THOÁT") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        builder.show()
    }


    // xóa khách hàng
    private fun showDeleteConfirmationDialog(khachHang: KhachHang) {
        AlertDialog.Builder(context)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa khách hàng này?")
            .setPositiveButton("XÓA") { dialog, _ ->
                databaseHelper.deleteKH(khachHang.maKH)
                Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show()
                updateKHList()
            }
            .setNegativeButton("HỦY") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // tìm kiếm khách hàng
    private fun filter(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            databaseHelper.getAllKH()
        } else {
            databaseHelper.searchKH(query)
        }
        adapter.updateList(filteredList)
    }

    private fun getKHList(): List<KhachHang> {
        return databaseHelper.getAllKH()
    }

    private fun updateKHList() {
        val updatedList = getKHList()
        adapter.updateList(updatedList)
    }
}
