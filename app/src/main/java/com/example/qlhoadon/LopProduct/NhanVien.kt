package com.example.qlhoadon.LopProduct

import java.io.Serializable

data class NhanVien(
    var maNV : String,
    val hoNV : String,
    val tenNV : String,
    val gioiTinh : String,
    val ngaySinh : String,
    val diaChi : String,
    val dienThoai : String,
    val noiSinh : String,
    val ngayVL : String,
    val emailNV : String
) : Serializable