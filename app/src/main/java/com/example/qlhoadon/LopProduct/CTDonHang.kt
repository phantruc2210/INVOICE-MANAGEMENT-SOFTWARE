package com.example.qlhoadon.LopProduct

import java.io.Serializable

data class CTDonHang (
    val maDH :String,
    val SP :String,
    val soLuong :Int,
    val giaBan :Double,
    val thanhTien :Double
) : Serializable