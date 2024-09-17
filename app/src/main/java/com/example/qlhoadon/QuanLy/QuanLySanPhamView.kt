package com.example.qlhoadon.QuanLy

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.example.qlhoadon.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class QuanLySanPhamView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var bottomNavi: BottomNavigationView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_quanlysanpham, this, true)
        setupView()
    }
    private fun setupView()
    {
        bottomNavi = findViewById(R.id.bottom_navi)
        val contentFrame = findViewById<FrameLayout>(R.id.FrameLayout)

        // Thiết lập Custom View mặc định khi khởi tạo
        val loaiSanPhamView = QLSPloaispView(context)
        contentFrame.addView(loaiSanPhamView)

        bottomNavi.setOnNavigationItemSelectedListener { item ->
            var selectedView: View? = null
            when (item.itemId) {
                R.id.item_lsp -> {
                    selectedView = QLSPloaispView(context)
                }
                R.id.item_sp -> {
                    selectedView = QLSPsanphamView(context)
                }
            }
            if (selectedView != null) {
                contentFrame.removeAllViews()
                contentFrame.addView(selectedView)
                true
            } else {
                false
            }
        }
    }
}
