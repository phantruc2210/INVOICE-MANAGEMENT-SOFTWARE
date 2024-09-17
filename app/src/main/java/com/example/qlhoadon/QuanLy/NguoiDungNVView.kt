package com.example.qlhoadon.QuanLy

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import com.example.qlhoadon.DatabaseHelper
import com.example.qlhoadon.R

class NguoiDungNVView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var id: EditText
    private lateinit var ho: EditText
    private lateinit var ten: EditText
    private lateinit var gt: EditText
    private lateinit var ngays: EditText
    private lateinit var diachi: EditText
    private lateinit var dt: EditText
    private lateinit var nois: EditText
    private lateinit var nvl: EditText
    private lateinit var email: EditText
    private lateinit var nutLuu: Button

    init {
        LayoutInflater.from(context).inflate(R.layout.view_nhanvien, this, true)
        // Khai báo sử dụng
        databaseHelper = DatabaseHelper(context)
        id = findViewById(R.id.edtID_NV)
        ho = findViewById(R.id.edtHo_NV)
        ten = findViewById(R.id.edtTen_NV)
        gt = findViewById(R.id.edtGioiTinh_NV)
        ngays = findViewById(R.id.edtDOB_NV)
        diachi = findViewById(R.id.edtDiaChi_NV)
        dt = findViewById(R.id.edtSDT_NV)
        nois = findViewById(R.id.edtNoiSinh_NV)
        nvl = findViewById(R.id.edtNVL_NV)
        email = findViewById(R.id.edtEmail_NV)
        nutLuu = findViewById(R.id.btnLuu_NV)

        nutLuu.setOnClickListener {
            updateEmployee()
        }
    }

    fun setEmployeeId(employeeId: String) {
        showDL(employeeId)
    }

    private fun showDL(employeeId: String) {
        val nvList = databaseHelper.getNVByID(employeeId)
        if (nvList.isNotEmpty()) {
            val nv = nvList[0]
            id.setText(nv.maNV)
            ho.setText(nv.hoNV)
            ten.setText(nv.tenNV)
            gt.setText(nv.gioiTinh)
            ngays.setText(nv.ngaySinh)
            diachi.setText(nv.diaChi)
            dt.setText(nv.dienThoai)
            nois.setText(nv.noiSinh)
            nvl.setText(nv.ngayVL)
            email.setText(nv.emailNV)
        }
    }

    private fun updateEmployee() {
        val employeeId = id.text.toString()
        val employeeHo = ho.text.toString()
        val employeeTen = ten.text.toString()
        val employeeGt = gt.text.toString()
        val employeeNgays = ngays.text.toString()
        val employeeDc = diachi.text.toString()
        val employeeDt = dt.text.toString()
        val employeeNois = nois.text.toString()
        val employeeNvl = nvl.text.toString()
        val employeeEmail = email.text.toString()

        if (employeeId.isNotEmpty())
        {
            databaseHelper.updateNV(employeeId, employeeHo, employeeTen, employeeGt, employeeNgays,
                 employeeDc, employeeDt, employeeNois, employeeNvl, employeeEmail )
            Toast.makeText(context, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show()
        }
        else
        {
            Toast.makeText(context, "Vui lòng nhập mã nhân viên", Toast.LENGTH_SHORT).show()
        }
    }
}
