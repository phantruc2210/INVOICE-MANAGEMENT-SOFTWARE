package com.example.qlhoadon.QuanLy

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qlhoadon.DatabaseHelper
import com.example.qlhoadon.LopAdapter.HoaDonAdapter
import com.example.qlhoadon.LopProduct.HoaDon
import com.example.qlhoadon.QuanLyEdit.EditHoaDonActivity
import com.example.qlhoadon.R
import java.io.Serializable

class QuanLyHoaDonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HoaDonAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var danhSachPTTT: List<String>

    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateHDList()
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_hoadon, this, true)
        databaseHelper = DatabaseHelper(context)
        setupView()
        registerReceiver()
    }

    private fun setupView() {
        recyclerView = findViewById(R.id.recyclerViewHD)
        recyclerView.layoutManager = LinearLayoutManager(context)

        danhSachPTTT = listOf("Chuyển khoản", "Tiền mặt")

        adapter = HoaDonAdapter(getHDList(), danhSachPTTT,
            onTTClick = { hoaDon ->
                val detailedHoaDon = databaseHelper.getHDByID(hoaDon.soHD)
                if (detailedHoaDon.isNotEmpty()) {
                    val intent = Intent(context, QLHDTTinHDActivity::class.java).apply {
                        putExtra("Hoadon", detailedHoaDon[0] as Serializable)
                    }
                    context.startActivity(intent)
                }
            },
            onEditClick = { hoaDon ->
                val detailedHoaDon = databaseHelper.getHDByID(hoaDon.soHD)
                if (detailedHoaDon.isNotEmpty()) {
                    val intent = Intent(context, EditHoaDonActivity::class.java).apply {
                        putExtra("Hoadon", detailedHoaDon[0] as Serializable)
                    }
                    context.startActivity(intent)
                }
            },
            onDeleteClick = { hoaDon ->
                showDeleteConfirmationDialog(hoaDon)
            })
        recyclerView.adapter = adapter

        val searchView = findViewById<SearchView>(R.id.searchViewHD)
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

    private fun registerReceiver() {
        LocalBroadcastManager.getInstance(context).registerReceiver(updateReceiver, IntentFilter("UPDATE_HD"))
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

    private fun showDeleteConfirmationDialog(hoaDon: HoaDon) {
        AlertDialog.Builder(context)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa hóa đơn này?")
            .setPositiveButton("XÓA") { dialog, _ ->
                databaseHelper.deleteHD(hoaDon.soHD)
                Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show()
                updateHDList()
            }
            .setNegativeButton("HỦY") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateHDList() {
        val updatedList = getHDList()
        adapter.updateList(updatedList)
    }

    private fun getHDList(): List<HoaDon> {
        return databaseHelper.getHD()
    }

    private fun filter(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            getHDList()
        } else {
            databaseHelper.searchHD(query)
        }
        adapter.updateList(filteredList)
    }
}
