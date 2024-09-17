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
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Spinner
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qlhoadon.DatabaseHelper
import com.example.qlhoadon.LopAdapter.SanPhamAdapter
import com.example.qlhoadon.LopProduct.LoaiSanPham
import com.example.qlhoadon.LopProduct.SanPham
import com.example.qlhoadon.QuanLyEdit.EditSanPhamActivity
import com.example.qlhoadon.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class QLSPsanphamView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var danhSachDonViTinh: List<String>
    private lateinit var danhSachLoaiSP: List<LoaiSanPham>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SanPhamAdapter
    private lateinit var databaseHelper: DatabaseHelper

    // xử lý update trong Broadcast
    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateSPList()
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_sanpham, this, true)
        databaseHelper = DatabaseHelper(context)
        setupView()
    }

    private fun setupView()
    {
        recyclerView = findViewById(R.id.recyclerViewSP)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Dữ liệu cho Spinner
        danhSachDonViTinh = listOf("Cái", "Cặp", "Bộ", "Hộp", "Quyển", "Cây")
        danhSachLoaiSP = databaseHelper.getAllLSP()

        adapter = SanPhamAdapter(getSPList(), danhSachDonViTinh, danhSachLoaiSP,
            // sửa sản phẩm
            onEditClick = { sanPham ->
                val intent = Intent(context, EditSanPhamActivity::class.java)
                intent.putExtra("sanPham", sanPham)
                context.startActivity(intent)
            },
            // xóa sản phẩm
            onDeleteClick = { sanPham ->
                showDeleteConfirmationDialog(sanPham)
            })

        recyclerView.adapter = adapter

        // thêm sản phẩm
        val floatingActionButton: FloatingActionButton = findViewById(R.id.floatButtonSP)
        floatingActionButton.setOnClickListener {
            showAddSanPhamDialog()
        }

        // Thiết lập tìm kiếm
        val searchView = findViewById<SearchView>(R.id.searchViewSP)
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
        LocalBroadcastManager.getInstance(context).registerReceiver(updateReceiver, IntentFilter("UPDATE_SP"))
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

    // thêm sản phẩm

    private fun showAddSanPhamDialog() {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(R.layout.add_sanpham, null)

        val LSP = dialogLayout.findViewById<Spinner>(R.id.spin_lsp_sp)
        val maSP = dialogLayout.findViewById<EditText>(R.id.masp)
        val tenSP = dialogLayout.findViewById<EditText>(R.id.tensp)
        val slTon = dialogLayout.findViewById<EditText>(R.id.slton)
        val giaBan = dialogLayout.findViewById<EditText>(R.id.giaban)
        val dvt = dialogLayout.findViewById<Spinner>(R.id.spin_dvt)
        val anhsp = dialogLayout.findViewById<EditText>(R.id.linkanh)


        // Thiết lập Adapter cho Spinner đơn vị tính
        val adapterDVT = ArrayAdapter(context, android.R.layout.simple_spinner_item, danhSachDonViTinh)
        adapterDVT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dvt.adapter = adapterDVT

        // Thiết lập Adapter cho Spinner loại sản phẩm
        val loaiSPNames = danhSachLoaiSP.map { it.tenloaiSP }
        val adapterLSP = ArrayAdapter(context, android.R.layout.simple_spinner_item, loaiSPNames)
        adapterLSP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        LSP.adapter = adapterLSP

        // Thay đổi thông tin khi mã sản phẩm thay đổi
        maSP.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val id = s.toString()
                if (id.isNotEmpty()) {
                    val sanPhamList = databaseHelper.getSPByID(id)
                    if (sanPhamList.isNotEmpty()) {
                        val sanPham = sanPhamList[0]
                        tenSP.setText(sanPham.tenSP)
                        slTon.setText(sanPham.slTon.toString())
                        giaBan.setText(sanPham.giaBan.toString())
                        dvt.setSelection(danhSachDonViTinh.indexOf(sanPham.donViTinh))
                        LSP.setSelection(loaiSPNames.indexOf(sanPham.loaiSP))
                        anhsp.setText(sanPham.anhSP)
                    } else {
                        tenSP.setText("")
                        slTon.setText("")
                        giaBan.setText("")
                        dvt.setSelection(0)
                        LSP.setSelection(0)
                        anhsp.setText("")
                    }
                }
            }
        })

        builder.setView(dialogLayout)
        builder.setTitle("Thêm Sản Phẩm")
        builder.setPositiveButton("THÊM") { dialogInterface, _ ->
            val id = maSP.text.toString()
            val name = tenSP.text.toString()
            val slton = slTon.text.toString().toIntOrNull()
            val gia = giaBan.text.toString().toDoubleOrNull()
            val donvi = dvt.selectedItem.toString()
            val namelsp = LSP.selectedItem.toString()
            val anh = anhsp.text.toString()
            if (id.isNotEmpty() && name.isNotEmpty() && slton != null &&
                gia != null && donvi.isNotEmpty() && namelsp.isNotEmpty()) {
                if(slton < 0)
                {
                    Toast.makeText(context, "Số lượng tồn phải lớn hơn bằng 0!", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    databaseHelper.addSP(id, name, slton, gia, donvi, namelsp, anh)
                    Toast.makeText(context, "Thêm thành công!", Toast.LENGTH_SHORT).show()
                    // xóa các ô
                    maSP.text.clear()
                    tenSP.text.clear()
                    slTon.text.clear()
                    giaBan.text.clear()
                    anhsp.text.clear()
                    dvt.setSelection(0)
                    LSP.setSelection(0)
                    updateSPList()
                }
            } else {
                Toast.makeText(context, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("THOÁT") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        builder.show()
    }



    // xóa sản phẩm
    private fun showDeleteConfirmationDialog(sanPham: SanPham) {
        AlertDialog.Builder(context)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này?")
            .setPositiveButton("XÓA") { dialog, _ ->
                databaseHelper.deleteSP(sanPham.maSP)
                Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show()
                updateSPList()
            }
            .setNegativeButton("HỦY") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // tìm kếm sản phẩm
    private fun filter(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            databaseHelper.getAllSP()
        } else {
            databaseHelper.searchSP(query)
        }
        adapter.updateList(filteredList)
    }

    private fun getSPList(): List<SanPham> {
        return databaseHelper.getAllSP()
    }

    private fun updateSPList() {
        val updatedList = getSPList()
        adapter.updateList(updatedList)
    }

}
