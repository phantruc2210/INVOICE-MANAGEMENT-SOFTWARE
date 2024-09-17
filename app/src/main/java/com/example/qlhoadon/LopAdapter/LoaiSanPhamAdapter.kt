package com.example.qlhoadon.LopAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.qlhoadon.LopProduct.LoaiSanPham
import com.example.qlhoadon.LopProduct.SanPham
import com.example.qlhoadon.R

class LoaiSanPhamAdapter(private var LoaiSanPhamList: List<LoaiSanPham>, private val onEditClick: (LoaiSanPham) -> Unit,
                         private val onDeleteClick: (LoaiSanPham) -> Unit) :
    RecyclerView.Adapter<LoaiSanPhamAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_loaisp, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sanPham = LoaiSanPhamList[position]
        holder.bind(sanPham)
    }

    override fun getItemCount(): Int {
        return LoaiSanPhamList.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val edtMaloaisanpham: EditText = itemView.findViewById(R.id.edtMaloaiSP)
        private val edtTenloaiSP: EditText = itemView.findViewById(R.id.edtTenloaiSP)
        private val edtGhiChu: EditText = itemView.findViewById(R.id.edtGhiChu)
        private val imageViewSua : ImageView = itemView.findViewById(R.id.imageViewSuaLSP)
        private val imageViewXoa : ImageView = itemView.findViewById(R.id.imageViewXoaLSP)

        fun bind(LoaiSanPham: LoaiSanPham) {
            edtMaloaisanpham.setText(LoaiSanPham.maloaiSP)
            edtTenloaiSP.setText(LoaiSanPham.tenloaiSP)
            edtGhiChu.setText(LoaiSanPham.ghiChu)

            imageViewSua.setOnClickListener {
                onEditClick(LoaiSanPham)
            }

            imageViewXoa.setOnClickListener {
                onDeleteClick(LoaiSanPham)
            }
        }
    }

    fun updateList(newList: List<LoaiSanPham>) {
        LoaiSanPhamList = newList
        notifyDataSetChanged()
    }
}