package com.example.qlhoadon.LopAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.qlhoadon.LopProduct.LoaiSanPham
import com.example.qlhoadon.LopProduct.SanPham
import com.example.qlhoadon.R

class SanPhamAdapter(
    private var sanPhamList: List<SanPham>,
    private val danhSachDonViTinh: List<String>,
    private val danhSachLoaiSP: List<LoaiSanPham>,
    private val onEditClick: (SanPham) -> Unit,
    private val onDeleteClick: (SanPham) -> Unit
) : RecyclerView.Adapter<SanPhamAdapter.ViewHolder>() {

    private val loaiSPMap = danhSachLoaiSP.associateBy { it.tenloaiSP }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_sanpham, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sanPham = sanPhamList[position]
        holder.bind(sanPham)
    }

    override fun getItemCount(): Int {
        return sanPhamList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val edtMaSP: EditText = itemView.findViewById(R.id.edtMaSP)
        private val edtTenSP: EditText = itemView.findViewById(R.id.edtTenSP)
        private val edtSlTon: EditText = itemView.findViewById(R.id.edtSLTon)
        private val edtGiaBan: EditText = itemView.findViewById(R.id.edtGiaBan)
        private val spinnerDVT: Spinner = itemView.findViewById(R.id.spinnerDVT)
        private val spinnerLSP: Spinner = itemView.findViewById(R.id.spinnerLSP)
        private val imageAnhSP: ImageView = itemView.findViewById(R.id.imageAnhSP)
        private val imageViewSua: ImageView = itemView.findViewById(R.id.imageViewSuaSP)
        private val imageViewXoa: ImageView = itemView.findViewById(R.id.imageViewXoaSP)

        fun bind(sanPham: SanPham) {
            edtMaSP.setText(sanPham.maSP)
            edtTenSP.setText(sanPham.tenSP)
            edtSlTon.setText(sanPham.slTon.toString())
            edtGiaBan.setText(sanPham.giaBan.toString())

            // Adapter cho spinnerDVT
            val adapterDVT = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_item, danhSachDonViTinh)
            adapterDVT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerDVT.adapter = adapterDVT
            spinnerDVT.setSelection(danhSachDonViTinh.indexOf(sanPham.donViTinh))
            // Chặn người dùng tương tác với Spinner
            spinnerDVT.setOnTouchListener { _, _ -> true }

            // Adapter cho spinnerLSP
            val loaiSPNames = danhSachLoaiSP.map { it.tenloaiSP }
            val adapterLSP = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_item, loaiSPNames)
            adapterLSP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerLSP.adapter = adapterLSP
            // Đặt lựa chọn cho Spinner loại sản phẩm
            val loaiSPName = loaiSPMap[sanPham.loaiSP]?.tenloaiSP ?: ""
            spinnerLSP.setSelection(loaiSPNames.indexOf(loaiSPName))
            // Chặn người dùng tương tác với Spinner
            spinnerLSP.setOnTouchListener { _, _ -> true }

            // Kiểm tra và hiển thị ảnh sản phẩm
            if (sanPham.anhSP.isNullOrEmpty()) {
                imageAnhSP.setImageResource(R.drawable.sanpham) // Hiển thị ảnh mặc định nếu ảnh sản phẩm là null
            } else {
                Glide.with(itemView.context) // Use itemView.context instead of context
                    .load(sanPham.anhSP)
                    .placeholder(R.drawable.sanpham) // Hiển thị ảnh mặc định trong khi tải ảnh từ URL
                    .into(imageAnhSP)
            }

            imageViewSua.setOnClickListener {
                onEditClick(sanPham)
            }

            imageViewXoa.setOnClickListener {
                onDeleteClick(sanPham)
            }
        }
    }

    fun updateList(newList: List<SanPham>) {
        sanPhamList = newList
        notifyDataSetChanged()
    }
}

