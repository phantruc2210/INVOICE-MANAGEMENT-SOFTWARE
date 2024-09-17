package com.example.qlhoadon.LopAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.example.qlhoadon.LopProduct.HoaDon
import com.example.qlhoadon.R

class HoaDonAdapter(
    private var hoaDonList: List<HoaDon>,
    private val danhSachPTTT: List<String>,
    private val onTTClick: (HoaDon) -> Unit,
    private val onEditClick: (HoaDon) -> Unit,
    private val onDeleteClick: (HoaDon) -> Unit
) : RecyclerView.Adapter<HoaDonAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_hoadon, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hoaDon = hoaDonList[position]
        holder.bind(hoaDon)
    }

    override fun getItemCount(): Int { return hoaDonList.size}

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val edtSoDH: EditText = itemView.findViewById(R.id.edtSoHD_L)
        private val edtNgayLap: EditText = itemView.findViewById(R.id.edtNgayLap_L)
        private val spinnerPTTT: Spinner = itemView.findViewById(R.id.spin_PTTT_L)
        private val edtMaDH: EditText = itemView.findViewById(R.id.edtMaDH_HD_L)
        private val imageViewTT: ImageView = itemView.findViewById(R.id.imageViewTTHD)
        private val imageViewSua: ImageView = itemView.findViewById(R.id.imageViewSuaHD)
        private val imageViewXoa: ImageView = itemView.findViewById(R.id.imageViewXoaHD)

        fun bind(hoaDon: HoaDon) {
            edtSoDH.setText(hoaDon.soHD)
            edtNgayLap.setText(hoaDon.ngayLap)
            edtMaDH.setText(hoaDon.maDH)

            // Adapter cho spinnerPTTT
            val adapterPTTT = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_item, danhSachPTTT)
            adapterPTTT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPTTT.adapter = adapterPTTT

            // Thiết lập lựa chọn dựa trên hoaDon.PTTT
            val index = danhSachPTTT.indexOf(hoaDon.PTTT.trim())
            if (index >= 0) {
                spinnerPTTT.setSelection(index)
            } else {
                // Nhật ký hoặc xử lý trường hợp không tìm thấy PTTT trong danhSachPTTT
                Log.e("HoaDonAdapter", "Không tìm thấy PTTT: ${hoaDon.PTTT}")
            }
            // Chặn người dùng tương tác với Spinner
            spinnerPTTT.setOnTouchListener { _, _ -> true }

            imageViewTT.setOnClickListener { onTTClick(hoaDon) }
            imageViewSua.setOnClickListener { onEditClick(hoaDon) }
            imageViewXoa.setOnClickListener { onDeleteClick(hoaDon) }
        }

    }


    fun updateList(newList: List<HoaDon>) {
        hoaDonList = newList
        notifyDataSetChanged()
    }
}
