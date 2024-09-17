package com.example.qlhoadon.LopAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.example.qlhoadon.LopProduct.CTDonHang
import com.example.qlhoadon.LopProduct.SanPham
import com.example.qlhoadon.R

class CTDonHangAdapter(
    private var CTDHList: List<CTDonHang>,
    private val danhSachSP: List<SanPham>,
    private val onEditClick: (CTDonHang) -> Unit,
    private val onDeleteClick: (CTDonHang) -> Unit
) : RecyclerView.Adapter<CTDonHangAdapter.ViewHolder>() {

    private val SPMap = danhSachSP.associateBy { it.tenSP }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_ctdonhang, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val CTDH = CTDHList[position]
        holder.bind(CTDH)
    }

    override fun getItemCount(): Int {
        return CTDHList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val spinner_sp_ctdh: Spinner = itemView.findViewById(R.id.spinner_sp_ctdh)
        private val edtSoLuong_ctdh: EditText = itemView.findViewById(R.id.edtSoLuong_ctdh)
        private val edtGia_ctdh: EditText = itemView.findViewById(R.id.edtGia_ctdh)
        private val edtThanhTien_ctdh: EditText = itemView.findViewById(R.id.edtThanhTien_ctdh)
        private val imageViewSua: ImageView = itemView.findViewById(R.id.imageViewSuaCTDH)
        private val imageViewXoa: ImageView = itemView.findViewById(R.id.imageViewXoaCTDH)

        fun bind(ctdh: CTDonHang) {

            val SPNames = danhSachSP.map{it.tenSP}
            val adapterSP = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_item, SPNames)
            adapterSP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_sp_ctdh.adapter = adapterSP
            val SPName = SPMap[ctdh.SP]?.tenSP ?: ""
            spinner_sp_ctdh.setSelection(SPNames.indexOf(SPName))
            // Chặn người dùng tương tác với Spinner
            spinner_sp_ctdh.setOnTouchListener { _, _ -> true }

            edtSoLuong_ctdh.setText(ctdh.soLuong.toString())
            edtGia_ctdh.setText(ctdh.giaBan.toString())
            edtThanhTien_ctdh.setText(ctdh.thanhTien.toString())

            imageViewSua.setOnClickListener {
                onEditClick(ctdh)
            }

            imageViewXoa.setOnClickListener {
                onDeleteClick(ctdh)
            }
        }
    }

    fun updateList(newList: List<CTDonHang>) {
        CTDHList = newList
        notifyDataSetChanged()
    }
}
