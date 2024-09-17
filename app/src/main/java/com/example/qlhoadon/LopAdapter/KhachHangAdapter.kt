package com.example.qlhoadon.LopAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.example.qlhoadon.LopProduct.KhachHang
import com.example.qlhoadon.R

class KhachHangAdapter(private var khachHangList: List<KhachHang>,
                       private val onEditClick: (KhachHang) -> Unit,
                       private val onDeleteClick: (KhachHang) -> Unit) :
    RecyclerView.Adapter<KhachHangAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_khachhang, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val khachHang = khachHangList[position]
        holder.bind(khachHang)
    }

    override fun getItemCount(): Int {
        return khachHangList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val edtMaKH: EditText = itemView.findViewById(R.id.edtMaKH)
        private val edtTenKH: EditText = itemView.findViewById(R.id.edtTenKH)
        private val edtDiaChi: EditText = itemView.findViewById(R.id.edtDiaChi)
        private val editNgaySinh: EditText = itemView.findViewById(R.id.edtNgaySinh)
        private val editDienThoai: EditText = itemView.findViewById(R.id.edtDienThoai)
        private val editEmail: EditText = itemView.findViewById(R.id.edtEmail)
        private val radCo : RadioButton = itemView.findViewById(R.id.radCo)
        private val radKhong : RadioButton = itemView.findViewById(R.id.radKhong)
        private val imageViewSua :ImageView = itemView.findViewById(R.id.imageViewSuaKH)
        private val imageViewXoa :ImageView = itemView.findViewById(R.id.imageViewXoaKH)

        fun bind(khachHang: KhachHang) {
            edtMaKH.setText(khachHang.maKH)
            edtTenKH.setText(khachHang.tenKH)
            edtDiaChi.setText(khachHang.diaChi)
            editNgaySinh.setText(khachHang.ngaySinh)
            editDienThoai.setText(khachHang.dienThoai)
            editEmail.setText(khachHang.email)
            if(khachHang.thanhVien == "Có")
            {
                radCo.isChecked = true
                radKhong.isChecked = false
            }
            else
            {
                radCo.isChecked = false
                radKhong.isChecked = true
            }

            imageViewSua.setOnClickListener {
                onEditClick(khachHang)
            }

            imageViewXoa.setOnClickListener {
                onDeleteClick(khachHang)
            }
        }
    }

    fun updateList(newList: List<KhachHang>) {
        khachHangList = newList
        notifyDataSetChanged()
    }
}