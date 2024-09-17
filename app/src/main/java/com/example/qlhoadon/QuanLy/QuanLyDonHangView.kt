package com.example.qlhoadon.QuanLy

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Spinner
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qlhoadon.DatabaseHelper
import com.example.qlhoadon.EmailSender
import com.example.qlhoadon.LopAdapter.DonHangAdapter
import com.example.qlhoadon.LopProduct.DonHang
import com.example.qlhoadon.LopProduct.HoaDon
import com.example.qlhoadon.LopProduct.KhachHang
import com.example.qlhoadon.LopProduct.NhanVien
import com.example.qlhoadon.QuanLyEdit.EditDonHangActivity
import com.example.qlhoadon.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class QuanLyDonHangView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DonHangAdapter
    private lateinit var danhSachTrangThai :List<String>
    private lateinit var danhSachNV :List<NhanVien>
    private lateinit var danhSachKH :List<KhachHang>
    private lateinit var databaseHelper: DatabaseHelper

    // xử lý update trong Broadcast
    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateDHList()
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_donhang, this, true)
        databaseHelper = DatabaseHelper(context)
        setupView()
    }

    private fun setupView() {
        recyclerView = findViewById(R.id.recyclerViewDH)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Dữ liệu cho Spinner
        danhSachTrangThai = listOf("Đặt hàng thành công", "Đã giao hàng", "Giao hàng thành công")
        danhSachKH = databaseHelper.getAllKH()
        danhSachNV = databaseHelper.getAllNV()

        adapter = DonHangAdapter(getDHList(), danhSachTrangThai, danhSachNV, danhSachKH,
            // sửa đơn hàng
            onEditClick = { donHang ->
                val intent = Intent(context, EditDonHangActivity::class.java)
                intent.putExtra("DonHang", donHang)
                context.startActivity(intent)
            },
            // xóa đơn hàng
            onDeleteClick = { donHang ->
                showDeleteConfirmationDialog(donHang)
            },
            // thêm chi tiết đơn hàng
            onCTDHClick = { donHang ->
                val intent = Intent(context, QLDHChiTietDHActivity::class.java)
                intent.putExtra("DonHang", donHang)
                context.startActivity(intent)},
            // tạo hóa đơn
            onTaoHDClick = {donHang ->
                val orderDetails = databaseHelper.getCTDHByID(donHang.maDH)
                if (orderDetails.isEmpty()) {
                    Toast.makeText(context, "Đơn hàng chưa có sản phẩm, không thể xuất hóa đơn.", Toast.LENGTH_SHORT).show()
                } else {
                    // Kiểm tra nếu đơn hàng đã có hóa đơn
                    if (databaseHelper.checkIfHDExistsForDH(donHang.maDH)) {
                        Toast.makeText(context, "Đơn hàng đã xuất hóa đơn", Toast.LENGTH_SHORT).show()
                    } else {
                        val intent = Intent(context, TaoHDActivity::class.java)
                        intent.putExtra("DonHang", donHang)
                        context.startActivity(intent)
                    }
                }
            })
        recyclerView.adapter = adapter

        // thêm đơn hàng
        val floatingActionButton: FloatingActionButton = findViewById(R.id.floatButtonDH)
        floatingActionButton.setOnClickListener {
            showAddDonHangDialog()
        }
        // Thiết lập tìm kiếm
        val searchView = findViewById<SearchView>(R.id.searchViewDH)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true
            }
        })
    }

    // xử lý broadcast
    private fun registerReceiver() {
        LocalBroadcastManager.getInstance(context).registerReceiver(updateReceiver, IntentFilter("UPDATE_DH"))
    }

    private fun unregisterReceiver() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(updateReceiver)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        registerReceiver()
    }

    override fun onDetachedFromWindow() {
        unregisterReceiver()
        super.onDetachedFromWindow()
    }

    private fun showAddDonHangDialog() {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(R.layout.add_donhang, null)

        val madh = dialogLayout.findViewById<EditText>(R.id.madh)
        val ngaydh = dialogLayout.findViewById<EditText>(R.id.ngaydh)
        val trangthai = dialogLayout.findViewById<Spinner>(R.id.spin_tthaidh)
        val nv = dialogLayout.findViewById<Spinner>(R.id.spin_nv_dh)
        val kh = dialogLayout.findViewById<Spinner>(R.id.spin_kh_dh)

        // Thiết lập Adapter cho Spinner trạng thái
        val adapterTT = ArrayAdapter(context, android.R.layout.simple_spinner_item, danhSachTrangThai)
        adapterTT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        trangthai.adapter = adapterTT

        // Thiết lập Adapter cho Spinner nhân viên
        val nvNames = danhSachNV.map { it.hoNV + " " + it.tenNV }
        val adapterNV = ArrayAdapter(context, android.R.layout.simple_spinner_item, nvNames)
        adapterNV.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        nv.adapter = adapterNV

        // Thiết lập Adapter cho Spinner khách hàng
        val khNames = danhSachKH.map { it.tenKH }
        val adapterKH = ArrayAdapter(context, android.R.layout.simple_spinner_item, khNames)
        adapterKH.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        kh.adapter = adapterKH

        // Thay đổi thông tin khi mã đơn hàng thay đổi
        madh.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val id = s.toString()
                if (id.isNotEmpty()) {
                    val donHangList = databaseHelper.getDHByID(id)
                    if (donHangList.isNotEmpty()) {
                        val donHang = donHangList[0]
                        ngaydh.setText(donHang.ngayDH)
                        trangthai.setSelection(danhSachTrangThai.indexOf(donHang.trangThai))
                        nv.setSelection(nvNames.indexOf(donHang.NV))
                        kh.setSelection(khNames.indexOf(donHang.KH))
                    } else {
                        ngaydh.setText("")
                        trangthai.setSelection(0)
                        nv.setSelection(0)
                        kh.setSelection(0)
                    }
                }
            }
        })

        builder.setView(dialogLayout)
        builder.setTitle("                 Thêm Đơn Hàng")
        builder.setPositiveButton("THÊM") { dialogInterface, _ ->
            val id = madh.text.toString()
            val ngay = ngaydh.text.toString()
            val tthai = trangthai.selectedItem.toString()
            val nhanvien = nv.selectedItem.toString()
            val khach = kh.selectedItem.toString()
            databaseHelper.addDH(id, ngay, tthai, nhanvien, khach)
            Toast.makeText(context, "Thêm thành công!", Toast.LENGTH_SHORT).show()
            // gửi email
            val dh = DonHang(id, ngay, tthai, nhanvien, khach)
            sendEmailBasedOnStatus(dh, tthai)
            // xóa các ô
            madh.text.clear()
            ngaydh.text.clear()
            trangthai.setSelection(0)
            nv.setSelection(0)
            kh.setSelection(0)
            updateDHList()
        }
        builder.setNegativeButton("THOÁT") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        builder.show()
    }


    private fun showDeleteConfirmationDialog(donHang: DonHang) {
        AlertDialog.Builder(context)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa đơn hàng này?")
            .setPositiveButton("XÓA") { dialog, _ ->
                databaseHelper.deleteDH(donHang.maDH)
                Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show()
                updateDHList()
            }
            .setNegativeButton("HỦY") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun getDHList(): List<DonHang> {
        return databaseHelper.getAllDH()
    }

    private fun updateDHList() {
        val updatedList = getDHList()
        adapter.updateList(updatedList)
    }

    // tìm kếm đơn hàng
    private fun filter(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            databaseHelper.getAllDH()
        } else {
            databaseHelper.searchDH(query)
        }
        adapter.updateList(filteredList)
    }
    // NỘI DUNG GỬI EMAIL
    // thông tin email
    private fun getKHList(KH: String): KhachHang? {
        val khList = databaseHelper.getKHByName(KH)
        return khList.firstOrNull()
    }
    // 1. Email Đơn đặt hàng thành công
    private fun sendOrderPlacedEmail(donHang: DonHang) {
        val khachHang = getKHList(donHang.KH)
        val email = khachHang?.email ?: return // Nếu không tìm thấy email, không gửi email

        val fromEmail = "2121005137@sv.ufm.edu.vn"
        val fromPassword = "Oin11469"
        val subject = "Moss thông báo Đặt hàng thành công"
        val body = """
    Xin chào ${khachHang?.tenKH},
    
    Chúng tôi xin gửi thông báo đặt hàng thành công:
      Mã đơn hàng:    ${donHang.maDH}
      Ngày đặt hàng:  ${donHang.ngayDH}
      Trạng thái:     ${donHang.trangThai}
    
    Thông tin khách hàng:
      Mã khách hàng:        ${khachHang?.maKH}
      Tên khách hàng:       ${khachHang?.tenKH}
      Địa chỉ:              ${khachHang?.diaChi}
      Ngày sinh:            ${khachHang?.ngaySinh}
      Điện thoại:           ${khachHang?.dienThoai}
      Email:                ${khachHang?.email}
      Tình trạng thành viên:${khachHang?.thanhVien}
    
    Cảm ơn bạn đã mua sắm tại cửa hàng của chúng tôi.
    """.trimIndent()

        Thread {
            EmailSender.sendEmail(fromEmail, fromPassword, email, subject, body)
        }.start()
    }
    // 2. Email Đơn đã giao hàng
    private fun sendOrderShippedEmail(donHang: DonHang, estimatedDeliveryTime: String) {
        val khachHang = getKHList(donHang.KH)
        val email = khachHang?.email ?: return

        val fromEmail = "2121005137@sv.ufm.edu.vn"
        val fromPassword = "Oin11469"
        val subject = "Moss thông báo Đơn hàng đã được giao cho nhà vận chuyển"

        val orderDetails = databaseHelper.getCTDHByID(donHang.maDH)
        val invoice = databaseHelper.getHDByIDDH(donHang.maDH).firstOrNull()

        val productDetails = orderDetails.joinToString(separator = " | ") { detail ->
            "Sản Phẩm: ${detail.SP}, Giá bán: ${detail.giaBan}, Số lượng: ${detail.soLuong}, Thành tiền: ${detail.thanhTien}"
        }

        val body = """
        Xin chào ${khachHang?.tenKH},
        
        Đơn hàng của bạn đã được giao cho nhà vận chuyển.
        Thời gian nhận hàng dự kiến: $estimatedDeliveryTime.
        
        Thông tin sản phẩm: 
        $productDetails
        
        Thông tin hóa đơn:
        Mã hóa đơn:            ${invoice?.soHD}
        Ngày lập hóa đơn:      ${invoice?.ngayLap}
        Phương thức thanh toán:${invoice?.PTTT}
        Phí vận chuyển:        ${invoice?.phiVC}
        Giảm giá:              ${invoice?.giamGia}
        Tổng hóa đơn:          ${invoice?.tongHD}
        
        Cảm ơn bạn đã mua sắm tại cửa hàng của chúng tôi.
    """.trimIndent()

        Thread {
            EmailSender.sendEmail(fromEmail, fromPassword, email, subject, body)
        }.start()
    }


    // 3. Đơn đã giao hàng thành công
    private fun sendOrderDeliveredEmail(donHang: DonHang) {
        val khachHang = getKHList(donHang.KH)
        val email = khachHang?.email ?: return

        val orderDetails = databaseHelper.getCTDHByID(donHang.maDH)
        val invoice = databaseHelper.getHDByIDDH(donHang.maDH).firstOrNull()

        val productDetails = orderDetails.joinToString(separator = " | ") { detail ->
            "Sản Phẩm: ${detail.SP}, Giá bán: ${detail.giaBan}, Số lượng: ${detail.soLuong}, Thành tiền: ${detail.thanhTien}"
        }

        val fromEmail = "2121005137@sv.ufm.edu.vn"
        val fromPassword = "Oin11469"
        val subject = "Moss thông báo Đơn hàng đã được giao hàng thành công"
        val body = """
        Xin chào ${khachHang.tenKH},
        
        Đơn hàng của bạn đã được giao hàng thành công. Hy vọng bạn hài lòng với sản phẩm.
        
        Thông tin sản phẩm: 
        $productDetails
                
        Thông tin hóa đơn:
        Mã hóa đơn:            ${invoice?.soHD}                
        Ngày lập hóa đơn:      ${invoice?.ngayLap}
        Phương thức thanh toán:${invoice?.PTTT}
        Phí vận chuyển:        ${invoice?.phiVC}
        Giảm giá:              ${invoice?.giamGia}
        Tổng hóa đơn:          ${invoice?.tongHD}
        
        Nếu cần hỗ trợ thêm, vui lòng liên hệ với chúng tôi qua email: 2121005137@sv.ufm.edu.vn hoặc 2121005303@sv.ufm.edu.vn.
        
        Cảm ơn bạn đã mua sắm tại cửa hàng của chúng tôi.
    """.trimIndent()

        Thread {
            EmailSender.sendEmail(fromEmail, fromPassword, email, subject, body)
        }.start()
    }
    // 4. Gửi email theo trạng thái đơn hàng
    fun sendEmailBasedOnStatus(donHang: DonHang, newStatus: String) {
        // Determine the estimated delivery time based on location
        val estimatedDeliveryTime = if (isWithinHCM(donHang)) {
            "từ 2-3 ngày làm việc (từ 08h đến 17h30 từ thứ Hai đến thứ Bảy)" +
                    "đối với các đơn hàng trong nội tỉnh Hồ Chí Minh"
        } else {
            "từ 4-6 ngày làm việc (từ 08h đến 17h30 từ thứ Hai đến thứ Bảy) " +
                    "đối với các đơn hàng ngoài tỉnh Hồ Chí Minh"
        }

        when (newStatus) {
            "Đặt hàng thành công" -> {
                sendOrderPlacedEmail(donHang)
            }
            "Đã giao hàng" -> {
                // Kiểm tra xem có hóa đơn cho đơn hàng không
                val hasInvoice = databaseHelper.getHDByIDDH(donHang.maDH).isNotEmpty()
                if (hasInvoice) {
                    sendOrderShippedEmail(donHang, estimatedDeliveryTime)
                }
            }
            "Giao hàng thành công" -> {
                // Kiểm tra xem có hóa đơn cho đơn hàng không
                val hasInvoice = databaseHelper.getHDByIDDH(donHang.maDH).isNotEmpty()
                if (hasInvoice) {
                    sendOrderDeliveredEmail(donHang)
                }
            }
        }
    }


    // Kiểm tra nếu địa chỉ trong thành phố Hồ Chí Minh
    fun isWithinHCM(donHang: DonHang): Boolean {
        val khachHang = getKHList(donHang.KH)
        val address = khachHang?.diaChi ?: ""

        // Regex mở rộng để kiểm tra nhiều biến thể của Hồ Chí Minh
        val regex = Regex(
            "(hồ chí minh|hcm|tp[. ]?hcm|tphcm|saigon|sài gòn|ho chi minh|ho chi minh city|hcm city)",
            RegexOption.IGNORE_CASE
        )

        return regex.containsMatchIn(address)
    }
}