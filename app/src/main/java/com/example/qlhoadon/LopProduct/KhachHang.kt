package com.example.qlhoadon.LopProduct

import java.io.Serializable

data class KhachHang(
    val maKH: String,
    val tenKH: String,
    val diaChi: String,
    val ngaySinh: String,
    val dienThoai: String,
    val email :String,
    val thanhVien: String
) : Serializable