package com.example.qlhoadon.LopAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qlhoadon.LopProduct.CTDonHang
import com.example.qlhoadon.LopProduct.SanPham
import com.example.qlhoadon.R

class CTHoaDonAdapter(
    private var CTDHList: List<CTDonHang>,
    private val danhSachSP: List<SanPham>,
) : RecyclerView.Adapter<CTHoaDonAdapter.ViewHolder>() {

    private val SPMap = danhSachSP.associateBy { it.tenSP }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cthd, parent, false)
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
        private val textViewTenSP: TextView = itemView.findViewById(R.id.textViewTenSP)
        private val textViewDonGia: TextView = itemView.findViewById(R.id.textViewDonGia)
        private val textViewSoLuong: TextView = itemView.findViewById(R.id.textViewSoLuong)
        private val textViewThanhTien: TextView = itemView.findViewById(R.id.textViewThanhTien)

        fun bind(ctdh: CTDonHang) {
            val sanPham = SPMap[ctdh.SP]
            textViewTenSP.text = sanPham?.tenSP ?: "Không tìm thấy sản phẩm"
            textViewSoLuong.text = ctdh.soLuong.toString()
            textViewDonGia.text = ctdh.giaBan.toString()
            textViewThanhTien.text = ctdh.thanhTien.toString()
        }
    }

    fun updateList(newList: List<CTDonHang>) {
        CTDHList = newList
        notifyDataSetChanged()
    }
}
