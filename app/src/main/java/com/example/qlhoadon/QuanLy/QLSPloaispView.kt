package com.example.qlhoadon.QuanLy

import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.sqlite.SQLiteDatabase
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qlhoadon.DatabaseHelper
import com.example.qlhoadon.LopAdapter.LoaiSanPhamAdapter
import com.example.qlhoadon.LopProduct.LoaiSanPham
import com.example.qlhoadon.QuanLyEdit.EditLoaiSPActivity
import com.example.qlhoadon.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class QLSPloaispView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LoaiSanPhamAdapter
    private lateinit var databaseHelper: DatabaseHelper

    // xử lý update trong Broadcast
    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateLSPList()
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_loaisp, this, true)
        databaseHelper = DatabaseHelper(context)
        setupView()
        registerReceiver()
    }

    private fun setupView() {
        recyclerView = findViewById(R.id.recyclerViewLSP)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = LoaiSanPhamAdapter(getLSPList(),
            onEditClick = { loaiSanPham ->
                val intent = Intent(context, EditLoaiSPActivity::class.java)
                intent.putExtra("loaiSanPham", loaiSanPham)
                context.startActivity(intent)
            },
            onDeleteClick = { loaiSanPham ->
                showDeleteConfirmationDialog(context, loaiSanPham)
            })
        recyclerView.adapter = adapter

        val floatingActionButton: FloatingActionButton = findViewById(R.id.floatButtonLSP)
        floatingActionButton.setOnClickListener {
            showAddLoaiSanPhamDialog(context)
        }
    }
    // xử lý broadcast
    private fun registerReceiver() {
        LocalBroadcastManager.getInstance(context).registerReceiver(updateReceiver, IntentFilter("UPDATE_LSP"))
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

    private fun showAddLoaiSanPhamDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(R.layout.add_loaisp, null)

        val maLSP = dialogLayout.findViewById<EditText>(R.id.malsp)
        val tenLSP = dialogLayout.findViewById<EditText>(R.id.tenlsp)
        val ghiChu = dialogLayout.findViewById<EditText>(R.id.ghichu_lsp)

        // Thay đổi thông tin khi mã loại sản phẩm thay đổi
        maLSP.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val id = s.toString()
                if (id.isNotEmpty()) {
                    val loaiSPList = databaseHelper.getLSPByID(id)
                    if (loaiSPList.isNotEmpty()) {
                        val loaisp = loaiSPList[0]
                        tenLSP.setText(loaisp.tenloaiSP)
                        ghiChu.setText(loaisp.ghiChu)
                    } else {
                        tenLSP.setText("")
                        ghiChu.setText("")
                    }
                }
            }
        })

        builder.setView(dialogLayout)
        builder.setTitle("Thêm Loại Sản Phẩm")
        builder.setPositiveButton("THÊM") { dialogInterface, _ ->
            val idlsp = maLSP.text.toString()
            val namelsp = tenLSP.text.toString()
            val note = ghiChu.text.toString()
            if (idlsp.isNotEmpty() && namelsp.isNotEmpty()) {
                databaseHelper.addLSP(idlsp, namelsp, note)
                Toast.makeText(context, "Thêm thành công!", Toast.LENGTH_SHORT).show()
                maLSP.text.clear()
                tenLSP.text.clear()
                ghiChu.text.clear()
                updateLSPList()
            } else {
                Toast.makeText(context, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("THOÁT") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        builder.show()
    }


    private fun showDeleteConfirmationDialog(context: Context, loaiSanPham: LoaiSanPham) {
        AlertDialog.Builder(context)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa loại sản phẩm này?")
            .setPositiveButton("XÓA") { dialog, _ ->
                databaseHelper.deleteLSP(loaiSanPham.maloaiSP)
                Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show()
                updateLSPList()
            }
            .setNegativeButton("HỦY") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun getLSPList(): List<LoaiSanPham> {
        return databaseHelper.getAllLSP()
    }

    private fun updateLSPList() {
        val updatedList = getLSPList()
        adapter.updateList(updatedList)
    }
}
