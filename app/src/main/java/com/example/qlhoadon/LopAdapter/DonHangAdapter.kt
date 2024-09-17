package com.example.qlhoadon.LopAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.example.qlhoadon.LopProduct.DonHang
import com.example.qlhoadon.LopProduct.KhachHang
import com.example.qlhoadon.LopProduct.NhanVien
import com.example.qlhoadon.LopProduct.SanPham
import com.example.qlhoadon.R

class DonHangAdapter (private var DonHangList :List<DonHang>, private val danhSachTrangThai: List<String>,
                      private val danhSachNV: List<NhanVien>, private val danhSachKH: List<KhachHang>,
                      private val onEditClick: (DonHang) -> Unit, private val onDeleteClick: (DonHang) -> Unit,
                      private val onCTDHClick :(DonHang) -> Unit, private val onTaoHDClick :(DonHang) -> Unit) : RecyclerView.Adapter<DonHangAdapter.ViewHolder>()
{
    private val khMap = danhSachKH.associateBy { it.tenKH }
    private val nvMap = danhSachNV.associateBy { it.hoNV + " " + it.tenNV }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonHangAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_donhang, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonHangAdapter.ViewHolder, position: Int) {
        val donhang = DonHangList[position]
        holder.bind(donhang)
    }

    override fun getItemCount(): Int {
        return DonHangList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val edtMaDH: EditText = itemView.findViewById(R.id.edtMaDH)
        private val edtNgayDH: EditText = itemView.findViewById(R.id.edtNgayDH)
        private val spinTrangThai: Spinner = itemView.findViewById(R.id.spin_TrangThai)
        private val spin_NV_DH: Spinner = itemView.findViewById(R.id.spinner_NV_DH)
        private val spin_KH_DH: Spinner = itemView.findViewById(R.id.spinner_KH_DH)
        private val imageViewSua: ImageView = itemView.findViewById(R.id.imageViewSuaDH)
        private val imageViewXoa: ImageView = itemView.findViewById(R.id.imageViewXoaDH)
        private val imageViewCTDH: ImageView = itemView.findViewById(R.id.imageViewCTDH)
        private val imageViewTaoHD: ImageView = itemView.findViewById(R.id.imageViewTaoHD)

        fun bind(DonHang: DonHang) {
            edtMaDH.setText(DonHang.maDH)
            edtNgayDH.setText(DonHang.ngayDH)

            // Thiết lập adapter cho spinner TrangThai
            val adapterTrangThai = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_item, danhSachTrangThai)
            adapterTrangThai.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinTrangThai.adapter = adapterTrangThai
            spinTrangThai.setSelection(danhSachTrangThai.indexOf(DonHang.trangThai))
            // Chặn người dùng tương tác với Spinner
            spinTrangThai.setOnTouchListener { _, _ -> true }

            // Thiết lập adapter cho spinner NV
            val nvNames = danhSachNV.map { it.hoNV + " " + it.tenNV }
            val adapterNV = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_item, nvNames)
            adapterNV.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spin_NV_DH.adapter = adapterNV
            val nvName = nvMap[DonHang.NV]?.let { it.hoNV + " " + it.tenNV } ?: ""
            spin_NV_DH.setSelection(nvNames.indexOf(nvName))
            // Chặn người dùng tương tác với Spinner
            spin_NV_DH.setOnTouchListener { _, _ -> true }

            // Thiết lập adapter cho spinner KH
            val khNames = danhSachKH.map { it.tenKH }
            val adapterKH = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_item, khNames)
            adapterKH.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spin_KH_DH.adapter = adapterKH
            val khName = khMap[DonHang.KH]?.tenKH ?: ""
            spin_KH_DH.setSelection(khNames.indexOf(khName))
            // Chặn người dùng tương tác với Spinner
            spin_KH_DH.setOnTouchListener { _, _ -> true }


            imageViewSua.setOnClickListener { onEditClick(DonHang) }
            imageViewXoa.setOnClickListener { onDeleteClick(DonHang) }
            imageViewCTDH.setOnClickListener { onCTDHClick(DonHang) }
            imageViewTaoHD.setOnClickListener { onTaoHDClick(DonHang) }
        }
    }

    fun updateList(newList: List<DonHang>) {
        DonHangList = newList
        notifyDataSetChanged()
    }

}