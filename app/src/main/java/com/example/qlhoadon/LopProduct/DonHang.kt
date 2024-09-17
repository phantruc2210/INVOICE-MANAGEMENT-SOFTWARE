package com.example.qlhoadon.LopProduct

import java.io.Serializable

data class DonHang (
    val maDH :String,
    var ngayDH :String,
    var trangThai :String,
    var NV :String,
    var KH :String
): Serializable
