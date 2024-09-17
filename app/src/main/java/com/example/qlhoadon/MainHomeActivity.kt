package com.example.qlhoadon

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.qlhoadon.QuanLy.HomeView
import com.example.qlhoadon.QuanLy.NguoiDungNVView
import com.example.qlhoadon.QuanLy.NguoiDungTKView
import com.example.qlhoadon.QuanLy.QuanLyDonHangView
import com.example.qlhoadon.QuanLy.QuanLyHoaDonView
import com.example.qlhoadon.QuanLy.QuanLyKhachHangView
import com.example.qlhoadon.QuanLy.QuanLySanPhamView
import com.example.qlhoadon.QuanLy.ThongKeTHKDView
import com.example.qlhoadon.QuanLy.ThongKeTTDHView
import com.google.android.material.navigation.NavigationView

class MainHomeActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var contentFrame: FrameLayout
    private lateinit var NVId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_home)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.NavigationView)
        toolbar = findViewById(R.id.ToolBar)
        contentFrame = findViewById(R.id.FrameLayout)

        // Nhận dữ liệu từ Intent
        val name = intent.getStringExtra("EXTRA_FULLNAME")
        val email = intent.getStringExtra("EXTRA_EMAIL")
        NVId = intent.getStringExtra("EXTRA_IDNV") ?: ""

        // Cập nhật header NavigationView
        val headerView = navView.getHeaderView(0)
        val textViewUser = headerView.findViewById<TextView>(R.id.textViewUser)
        val textViewEmail = headerView.findViewById<TextView>(R.id.textViewEmail)

        textViewUser.text = name ?: "TÊN NGƯỜI DÙNG"
        textViewEmail.text = email ?: "Email người dùng"

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu_24)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true

            when (menuItem.itemId) {
                R.id.nav_SP -> {
                    replaceCustomView(QuanLySanPhamView(this), "Quản Lý Sản Phẩm")
                }
                R.id.nav_KH -> {
                    replaceCustomView(QuanLyKhachHangView(this), "Quản Lý Khách Hàng")
                }
                R.id.nav_DH -> {
                    replaceCustomView(QuanLyDonHangView(this), "Quản Lý Đơn Hàng")
                }
                R.id.nav_HD -> {
                    replaceCustomView(QuanLyHoaDonView(this), "Quản Lý Hóa Đơn")
                }
                R.id.nav_TK_TTDH -> {
                    replaceCustomView(ThongKeTTDHView(this), "Thống Kê Tình Trạng Đơn Hàng")
                }
                R.id.nav_TK_THKD -> {
                    replaceCustomView(ThongKeTHKDView(this), "Thống Kê Tình Hình Kinh Doanh")
                }
                R.id.nav_ND_NV -> {
                    val nguoiDungNVView = NguoiDungNVView(this)
                    nguoiDungNVView.setEmployeeId(NVId)
                    replaceCustomView(nguoiDungNVView, "Quản Lý Nhân Viên")
                }
                R.id.nav_ND_TK -> {
                    val nguoiDungTKView = NguoiDungTKView(this)
                    nguoiDungTKView.setTKId(NVId)
                    replaceCustomView(nguoiDungTKView, "Quản Lý Tài Khoản")
                }
                R.id.nav_ND_DX -> {
                    showLogoutConfirmationDialog()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
        // Cài đặt view mặc định
        if (savedInstanceState == null) {
            replaceCustomView(HomeView(this), "Trang Chủ")
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Xác nhận đăng xuất")
            setMessage("Bạn có chắc chắn muốn đăng xuất không?")
            setPositiveButton("Đồng ý") { dialog, _ ->
                // Đăng xuất và quay lại màn hình ban đầu
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finishAffinity() // Đóng tất cả các hoạt động hiện tại
                dialog.dismiss()
            }
            setNegativeButton("Hủy") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    private fun replaceCustomView(customView: View, title: String) {
        contentFrame.removeAllViews()
        contentFrame.addView(customView)
        toolbar.title = title
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressedDispatcher.onBackPressed()
        }
    }
}
