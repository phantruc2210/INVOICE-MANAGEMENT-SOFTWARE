package com.example.qlhoadon.LopProduct

import java.io.Serializable

data class HoaDon(
    val soHD :String,
    val ngayLap :String,
    val PTTT : String,
    val phiVC :Double,
    val giamGia : Double,
    val tongHD : Double,
    val maDH :String
): Serializable