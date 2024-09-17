package com.example.qlhoadon.LopProduct

import java.io.Serializable

data class SanPham(
    val maSP: String,
    var tenSP: String,
    var slTon: Int,
    var giaBan: Double,
    var donViTinh: String,
    var loaiSP: String,
    val anhSP: String
): Serializable