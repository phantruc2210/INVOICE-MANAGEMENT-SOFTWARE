package com.example.qlhoadon

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import com.example.qlhoadon.LopProduct.CTDonHang
import com.example.qlhoadon.LopProduct.DonHang
import com.example.qlhoadon.LopProduct.HoaDon
import com.example.qlhoadon.LopProduct.KhachHang
import com.example.qlhoadon.LopProduct.LoaiSanPham
import com.example.qlhoadon.LopProduct.NhanVien
import com.example.qlhoadon.LopProduct.SanPham
import com.example.qlhoadon.LopProduct.Statistic
import com.example.qlhoadon.LopProduct.TaiKhoan
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "QLHoaDon.db"
        const val DATABASE_VERSION = 1

        // bảng loại sp
        const val TABLE_LOAISANPHAM = "LoaiSanPham"
        const val COLUMN_LSP_ID = "lsp_id"
        const val COLUMN_LSP_TEN = "lsp_ten"
        const val COLUMN_LSP_GHICHU = "lsp_ghichu"

        // bảng sp
        const val TABLE_SANPHAM = "SanPham"
        const val COLUMN_SP_ID = "sp_id"
        const val COLUMN_SP_TEN = "sp_ten"
        const val COLUMN_SP_SLTON = "sp_slton"
        const val COLUMN_SP_GIA = "sp_gia"
        const val COLUMN_SP_DVT = "sp_dvt"
        const val COLUMN_SP_LSP = "sp_lsp"
        const val COLUMN_SP_ANH = "sp_anh"

        // bảng kh
        const val TABLE_KHACHHANG = "KhachHang"
        const val COLUMN_KH_ID = "kh_id"
        const val COLUMN_KH_TEN = "kh_ten"
        const val COLUMN_KH_DIACHI = "kh_diachi"
        const val COLUMN_KH_NGAYSINH = "kh_ngaysinh"
        const val COLUMN_KH_DIENTHOAI = "kh_dienthoai"
        const val COLUMN_KH_EMAIL = "kh_email"
        const val COLUMN_KH_TVIEN = "kh_tvien"

        // bảng đơn hàng
        const val TABLE_DONHANG = "DonHang"
        const val COLUMN_DH_ID = "dh_id"
        const val COLUMN_DH_NGAYDH = "dh_ngaydh"
        const val COLUMN_DH_TRANGTHAI = "dh_trangthai"
        const val COLUMN_DH_IDKH = "dh_idkh"
        const val COLUMN_DH_IDNV = "dh_idnv"

        // bảng chi tiết đơn hàng
        const val TABLE_CT_DONHANG = "CTDonHang"
        const val COLUMN_CTDH_IDDH = "ctdh_iddh"
        const val COLUMN_CTDH_IDSP = "ctdh_idsp"
        const val COLUMN_CTDH_SL = "ctdh_soluong"
        const val COLUMN_CTDH_GIA = "ctdh_gia"
        const val COLUMN_CTDH_TIEN = "ctdh_tien"

        // bảng hóa đơn
        const val TABLE_HOADON = "HoaDon"
        const val COLUMN_HD_ID = "hd_idhd"
        const val COLUMN_HD_NGAYLAP = "hd_ngaylap"
        const val COLUMN_HD_PTTT = "hd_pttt"
        const val COLUMN_HD_PHIVC = "hd_phivc"
        const val COLUMN_HD_GIAMGIA = "hd_giamgia"
        const val COLUMN_HD_TONGHD = "hd_tonghd"
        const val COLUMN_HD_IDDH = "hd_iddh"

        // bảng nhân viên
        const val TABLE_NHANVIEN = "NhanVien"
        const val COLUMN_NV_ID = "nv_id"
        const val COLUMN_NV_HO = "nv_ho"
        const val COLUMN_NV_TEN = "nv_ten"
        const val COLUMN_NV_GIOITINH = "nv_gioitinh"
        const val COLUMN_NV_NGAYSINH = "nv_ngaysinh"
        const val COLUMN_NV_DIACHI = "nv_diachi"
        const val COLUMN_NV_DIENTHOAI = "nv_dienthoai"
        const val COLUMN_NV_NOISINH = "nv_noisinh"
        const val COLUMN_NV_NGAYVL = "nv_ngayvl"
        const val COLUMN_NV_EMAIL = "nv_email"

        // bảng tài khoản
        const val TABLE_TAIKHOAN = "TaiKhoan"
        const val COLUMN_TK_TENDN = "tk_tendn"
        const val COLUMN_TK_MATKHAU = "tk_matkhau"
        const val COLUMN_TK_IDNV = "tk_idnv"
    }
    // sao chép dữ liệu

    private val dbPath = context.getDatabasePath(DATABASE_NAME).path
    private val appContext = context.applicationContext

    init
    {
        if (!databaseExists())
        {
            this.readableDatabase.close()
            try
            {
                copyDatabaseFromAssets()
            } catch (e: IOException)
            {
                throw RuntimeException("Lỗi sao chép database", e)
            }
        }
    }

    private fun databaseExists(): Boolean
    {
        val dbFile = File(dbPath)
        return dbFile.exists()
    }

    private fun copyDatabaseFromAssets()
    {
        val inputStream = appContext.assets.open(DATABASE_NAME)
        val outputStream = FileOutputStream(dbPath)

        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0)
        {
            outputStream.write(buffer, 0, length)
        }

        outputStream.flush()
        outputStream.close()
        inputStream.close()
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // tạo bảng lsp
        val CREATE_TABLE_LOAISANPHAM = """
        CREATE TABLE $TABLE_LOAISANPHAM (
            $COLUMN_LSP_ID TEXT PRIMARY KEY,
            $COLUMN_LSP_TEN TEXT,
            $COLUMN_LSP_GHICHU TEXT
        )
    """

        // tạo bảng sp
        val CREATE_TABLE_SANPHAM = """
        CREATE TABLE $TABLE_SANPHAM (
            $COLUMN_SP_ID TEXT PRIMARY KEY,
            $COLUMN_SP_TEN TEXT,
            $COLUMN_SP_SLTON INTEGER,
            $COLUMN_SP_GIA DOUBLE,
            $COLUMN_SP_DVT TEXT,
            $COLUMN_SP_LSP TEXT,
            $COLUMN_SP_ANH TEXT,
            FOREIGN KEY($COLUMN_SP_LSP) REFERENCES $TABLE_LOAISANPHAM($COLUMN_LSP_ID) ON DELETE CASCADE
        )
    """

        // tạo bảng kh
        val CREATE_TABLE_KHACHHANG = """
        CREATE TABLE $TABLE_KHACHHANG (
            $COLUMN_KH_ID TEXT PRIMARY KEY,
            $COLUMN_KH_TEN TEXT,
            $COLUMN_KH_DIACHI TEXT,
            $COLUMN_KH_NGAYSINH TEXT,
            $COLUMN_KH_DIENTHOAI TEXT,
            $COLUMN_KH_EMAIL TEXT,
            $COLUMN_KH_TVIEN TEXT
        )
    """

        // tạo bảng đơn hàng
        val CREATE_TABLE_DONHANG = """
        CREATE TABLE $TABLE_DONHANG (
            $COLUMN_DH_ID TEXT PRIMARY KEY,
            $COLUMN_DH_NGAYDH TEXT,
            $COLUMN_DH_TRANGTHAI TEXT,
            $COLUMN_DH_IDKH TEXT,
            $COLUMN_DH_IDNV TEXT,
            FOREIGN KEY($COLUMN_DH_IDKH) REFERENCES $TABLE_KHACHHANG($COLUMN_KH_ID) ON DELETE CASCADE,
            FOREIGN KEY($COLUMN_DH_IDNV) REFERENCES $TABLE_NHANVIEN($COLUMN_NV_ID) ON DELETE CASCADE
        )
    """

        // tạo bảng chi tiết đơn hàng
        val CREATE_TABLE_CT_DONHANG = """
        CREATE TABLE $TABLE_CT_DONHANG (
            $COLUMN_CTDH_IDDH TEXT,
            $COLUMN_CTDH_IDSP TEXT,
            $COLUMN_CTDH_SL INTEGER,
            $COLUMN_CTDH_GIA REAL,
            $COLUMN_CTDH_TIEN REAL,
            PRIMARY KEY($COLUMN_CTDH_IDDH, $COLUMN_CTDH_IDSP),
            FOREIGN KEY($COLUMN_CTDH_IDDH) REFERENCES $TABLE_DONHANG($COLUMN_DH_ID) ON DELETE CASCADE,
            FOREIGN KEY($COLUMN_CTDH_IDSP) REFERENCES $TABLE_SANPHAM($COLUMN_SP_ID) ON DELETE CASCADE
        )
    """

        // tạo bảng hóa đơn
        val CREATE_TABLE_HOADON = """
        CREATE TABLE $TABLE_HOADON (
            $COLUMN_HD_ID TEXT PRIMARY KEY,
            $COLUMN_HD_NGAYLAP TEXT,
            $COLUMN_HD_PTTT TEXT,
            $COLUMN_HD_PHIVC DOUBLE,
            $COLUMN_HD_GIAMGIA DOUBLE,
            $COLUMN_HD_TONGHD DOUBLE,
            $COLUMN_HD_IDDH TEXT,
            FOREIGN KEY($COLUMN_HD_IDDH) REFERENCES $TABLE_DONHANG($COLUMN_DH_ID) ON DELETE CASCADE
        )
    """

        // tạo bảng nhân viên
        val CREATE_TABLE_NHANVIEN = """
        CREATE TABLE $TABLE_NHANVIEN (
            $COLUMN_NV_ID TEXT PRIMARY KEY,
            $COLUMN_NV_HO TEXT,
            $COLUMN_NV_TEN TEXT,
            $COLUMN_NV_GIOITINH TEXT,
            $COLUMN_NV_NGAYSINH TEXT,
            $COLUMN_NV_DIACHI TEXT,
            $COLUMN_NV_DIENTHOAI TEXT,
            $COLUMN_NV_NOISINH TEXT,
            $COLUMN_NV_NGAYVL TEXT,
            $COLUMN_NV_EMAIL TEXT
        )
    """

        // tạo bảng tài khoản
        val CREATE_TABLE_TAIKHOAN = """
        CREATE TABLE $TABLE_TAIKHOAN (
            $COLUMN_TK_TENDN TEXT PRIMARY KEY,
            $COLUMN_TK_MATKHAU TEXT,
            $COLUMN_TK_IDNV TEXT,
            FOREIGN KEY($COLUMN_TK_IDNV) REFERENCES $TABLE_NHANVIEN($COLUMN_NV_ID)
        )
    """
        // lệnh tạo các bảng
        db?.execSQL(CREATE_TABLE_LOAISANPHAM)
        db?.execSQL(CREATE_TABLE_SANPHAM)
        db?.execSQL(CREATE_TABLE_KHACHHANG)
        db?.execSQL(CREATE_TABLE_DONHANG)
        db?.execSQL(CREATE_TABLE_CT_DONHANG)
        db?.execSQL(CREATE_TABLE_HOADON)
        db?.execSQL(CREATE_TABLE_NHANVIEN)
        db?.execSQL(CREATE_TABLE_TAIKHOAN)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_LOAISANPHAM")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SANPHAM")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_KHACHHANG")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_DONHANG")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CT_DONHANG")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_HOADON")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NHANVIEN")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TAIKHOAN")
        onCreate(db)
    }

    // 1. LOẠI SẢN PHẨM
    // hiển thị
    fun getAllLSP(): List<LoaiSanPham> {
        val LSPList = mutableListOf<LoaiSanPham>()
        val select = "SELECT * FROM $TABLE_LOAISANPHAM"
        val db = this.readableDatabase
        val cursor = db.rawQuery(select, null)
        if (cursor.moveToFirst()) {
            do {
                val lsp = LoaiSanPham(
                    maloaiSP = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LSP_ID)),
                    tenloaiSP = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LSP_TEN)),
                    ghiChu = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LSP_GHICHU))
                )
                LSPList.add(lsp)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return LSPList
    }

    // hiển thị lsp theo id
    fun getLSPByID(id: String): List<LoaiSanPham> {
        val LSPList = mutableListOf<LoaiSanPham>()
        val select = "SELECT * FROM $TABLE_LOAISANPHAM WHERE $COLUMN_LSP_ID = ? "
        val db = this.readableDatabase
        val cursor = db.rawQuery(select, arrayOf(id))
        if (cursor.moveToFirst()) {
            do {
                val lsp = LoaiSanPham(
                    maloaiSP = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LSP_ID)),
                    tenloaiSP = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LSP_TEN)),
                    ghiChu = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LSP_GHICHU))
                )
                LSPList.add(lsp)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return LSPList
    }

    // thêm
    fun addLSP(id: String, ten: String, ghichu: String) {
        val db = this.writableDatabase

        // Kiểm tra nếu lsp_id đã tồn tại
        val cursor = db.rawQuery("SELECT ${DatabaseHelper.COLUMN_LSP_ID} FROM ${DatabaseHelper.TABLE_LOAISANPHAM} WHERE ${DatabaseHelper.COLUMN_LSP_ID} = ?", arrayOf(id))
        if (cursor.count > 0) {
            // Nếu tồn tại, cập nhật bản ghi hiện có
            val update = "UPDATE ${DatabaseHelper.TABLE_LOAISANPHAM} SET " +
                    "${DatabaseHelper.COLUMN_LSP_TEN} = '$ten', " +
                    "${DatabaseHelper.COLUMN_LSP_GHICHU} = '$ghichu' " +
                    "WHERE ${DatabaseHelper.COLUMN_LSP_ID} = '$id'"
            db.execSQL(update)
        } else {
            // Nếu không tồn tại, thêm bản ghi mới
            val insert = "INSERT INTO ${DatabaseHelper.TABLE_LOAISANPHAM} " +
                    "(${DatabaseHelper.COLUMN_LSP_ID}, ${DatabaseHelper.COLUMN_LSP_TEN}, ${DatabaseHelper.COLUMN_LSP_GHICHU}) " +
                    "VALUES('$id', '$ten', '$ghichu')"
            db.execSQL(insert)
        }
        cursor.close()
        db.close()
    }

    // sửa
    fun updateLSP(id: String, ten: String, ghichu: String) {
        val db = this.writableDatabase
        val update = "UPDATE ${DatabaseHelper.TABLE_LOAISANPHAM} SET " +
                "${DatabaseHelper.COLUMN_LSP_TEN} = '$ten', " +
                "${DatabaseHelper.COLUMN_LSP_GHICHU} = '$ghichu' " +
                "WHERE ${DatabaseHelper.COLUMN_LSP_ID} = '$id'"
        db?.execSQL(update)
        db.close()
    }

    // xóa
    fun deleteLSP(id: String) {
        val db = this.writableDatabase
        val delete =
            "DELETE FROM ${DatabaseHelper.TABLE_LOAISANPHAM} WHERE ${DatabaseHelper.COLUMN_LSP_ID} = '$id'"
        db?.execSQL(delete)
        db.close()
    }

    //  lấy tên loại sản phẩm
    fun getNameLSP(): List<String> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_LOAISANPHAM", null)
        val namelsp = mutableListOf<String>()
        if (cursor.moveToFirst()) {
            do {
                namelsp.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LSP_TEN)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return namelsp
    }

    // lấy id từ tên lsp
    fun getIDlspByName(namelsp: String): String {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_LSP_ID FROM $TABLE_LOAISANPHAM " +
                    "WHERE $COLUMN_LSP_TEN = ?", arrayOf(namelsp)
        )
        var id = ""
        if (cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LSP_ID))
        }
        cursor.close()
        return id
    }

    // 2. SẢN PHẨM
    // hiển thị
    fun getAllSP(): List<SanPham> {
        val SPList = mutableListOf<SanPham>()
        val select =
            "SELECT $COLUMN_SP_ID, $COLUMN_SP_TEN, $COLUMN_SP_SLTON, $COLUMN_SP_GIA, $COLUMN_SP_DVT, $COLUMN_LSP_TEN, $COLUMN_SP_ANH " +
                    "FROM $TABLE_SANPHAM JOIN $TABLE_LOAISANPHAM ON $TABLE_SANPHAM.$COLUMN_SP_LSP = $TABLE_LOAISANPHAM.$COLUMN_LSP_ID "
        val db = this.readableDatabase
        val cursor = db.rawQuery(select, null)
        if (cursor.moveToFirst()) {
            do {
                val sp = SanPham(
                    maSP = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SP_ID)),
                    tenSP = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SP_TEN)),
                    slTon = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SP_SLTON)),
                    giaBan = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SP_GIA)),
                    donViTinh = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SP_DVT)),
                    loaiSP = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LSP_TEN)),
                    anhSP = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SP_ANH))
                )
                SPList.add(sp)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return SPList
    }

    // hiển thị sp theo id
    fun getSPByID(id: String): List<SanPham> {
        val SPList = mutableListOf<SanPham>()
        val select =
            "SELECT $COLUMN_SP_ID, $COLUMN_SP_TEN, $COLUMN_SP_SLTON, $COLUMN_SP_GIA, $COLUMN_SP_DVT, $COLUMN_LSP_TEN, $COLUMN_SP_ANH " +
                    "FROM $TABLE_SANPHAM JOIN $TABLE_LOAISANPHAM ON $TABLE_SANPHAM.$COLUMN_SP_LSP = $TABLE_LOAISANPHAM.$COLUMN_LSP_ID " +
                    "WHERE $COLUMN_SP_ID = ? "
        val db = this.readableDatabase
        val cursor = db.rawQuery(select, arrayOf(id))
        if (cursor.moveToFirst()) {
            do {
                val sp = SanPham(
                    maSP = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SP_ID)),
                    tenSP = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SP_TEN)),
                    slTon = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SP_SLTON)),
                    giaBan = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SP_GIA)),
                    donViTinh = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SP_DVT)),
                    loaiSP = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LSP_TEN)),
                    anhSP = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SP_ANH))
                )
                SPList.add(sp)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return SPList
    }

    // thêm
    fun addSP(id: String, name: String, slton: Int, gia: Double, dvt: String, namelsp: String, anh: String) {
        val db = this.writableDatabase

        // Kiểm tra nếu sp_id đã tồn tại
        val cursor = db.rawQuery("SELECT ${DatabaseHelper.COLUMN_SP_ID} FROM ${DatabaseHelper.TABLE_SANPHAM} WHERE ${DatabaseHelper.COLUMN_SP_ID} = ?", arrayOf(id))
        if (cursor.count > 0) {
            // Nếu tồn tại, cập nhật bản ghi hiện có
            val update = "UPDATE ${DatabaseHelper.TABLE_SANPHAM} SET " +
                    "${DatabaseHelper.COLUMN_SP_TEN} = '$name', " +
                    "${DatabaseHelper.COLUMN_SP_SLTON} = '$slton', " +
                    "${DatabaseHelper.COLUMN_SP_GIA} = '$gia', " +
                    "${DatabaseHelper.COLUMN_SP_DVT} = '$dvt', " +
                    "${DatabaseHelper.COLUMN_SP_LSP} = '${getIDlspByName(namelsp)}', " +
                    "${DatabaseHelper.COLUMN_SP_ANH} = '$anh' " +
                    "WHERE ${DatabaseHelper.COLUMN_SP_ID} = '$id'"
            db.execSQL(update)
        } else {
            // Nếu không tồn tại, thêm bản ghi mới
            val insert = "INSERT INTO ${DatabaseHelper.TABLE_SANPHAM} " +
                    "(${DatabaseHelper.COLUMN_SP_ID}, ${DatabaseHelper.COLUMN_SP_TEN}, ${DatabaseHelper.COLUMN_SP_SLTON}, " +
                    "${DatabaseHelper.COLUMN_SP_GIA}, ${DatabaseHelper.COLUMN_SP_DVT}, ${DatabaseHelper.COLUMN_SP_LSP}, " +
                    "${DatabaseHelper.COLUMN_SP_ANH}) " +
                    "VALUES('$id', '$name', '$slton', '$gia', '$dvt', '${getIDlspByName(namelsp)}', '$anh')"
            db.execSQL(insert)
        }
        cursor.close()
        db.close()
    }


    // sửa
    fun updateSP(id: String, name: String, slton: Int, gia: Double, dvt: String, namelsp: String, anh :String) {
        val db = this.writableDatabase
        val update = "UPDATE ${DatabaseHelper.TABLE_SANPHAM}" +
                " SET ${DatabaseHelper.COLUMN_SP_TEN} = '$name', " +
                "${DatabaseHelper.COLUMN_SP_SLTON} = '$slton', " +
                "${DatabaseHelper.COLUMN_SP_GIA} = '$gia', " +
                "${DatabaseHelper.COLUMN_SP_DVT} = '$dvt', " +
                "${DatabaseHelper.COLUMN_SP_LSP} = '${getIDlspByName(namelsp)}', " +
                "${DatabaseHelper.COLUMN_SP_ANH} = '$anh' " +
                "WHERE ${DatabaseHelper.COLUMN_SP_ID} = '$id' "
        db?.execSQL(update)
        db.close()
    }

    // xóa
    fun deleteSP(id: String) {
        val db = this.writableDatabase
        val delete =
            "DELETE FROM ${DatabaseHelper.TABLE_SANPHAM} WHERE ${DatabaseHelper.COLUMN_SP_ID} = '$id'"
        db?.execSQL(delete)
        db.close()
    }

    // tìm kiếm
    fun searchSP(query: String): List<SanPham> {
        val SPList = mutableListOf<SanPham>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_SP_ID, $COLUMN_SP_TEN, $COLUMN_SP_SLTON, $COLUMN_SP_GIA, $COLUMN_SP_DVT, $COLUMN_LSP_TEN, $COLUMN_SP_ANH " +
                    "FROM $TABLE_SANPHAM JOIN $TABLE_LOAISANPHAM ON $TABLE_SANPHAM.$COLUMN_SP_LSP = $TABLE_LOAISANPHAM.$COLUMN_LSP_ID" +
                    " WHERE $COLUMN_SP_TEN LIKE ? OR $COLUMN_SP_ID LIKE ? OR $COLUMN_SP_LSP LIKE ? " +
                    "OR $COLUMN_SP_SLTON LIKE ? OR $COLUMN_SP_GIA LIKE ? OR $COLUMN_SP_DVT LIKE ? " +
                    "OR $COLUMN_LSP_TEN LIKE ? ",
            arrayOf(
                "%$query%", "%$query%", "%${getIDspByName(query)}%",
                "%$query%", "%$query%", "%$query%", "%$query%")
        )
        if (cursor.moveToFirst()) {
            do {
                val sp = SanPham(
                    maSP = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SP_ID)),
                    tenSP = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SP_TEN)),
                    slTon = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SP_SLTON)),
                    giaBan = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SP_GIA)),
                    donViTinh = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SP_DVT)),
                    loaiSP = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LSP_TEN)),
                    anhSP = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SP_ANH))
                )
                SPList.add(sp)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return SPList
    }


    // lấy id từ tên sp
    fun getIDspByName(Name: String): String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_SP_ID FROM $TABLE_SANPHAM " +
                    "WHERE $COLUMN_SP_TEN = ?", arrayOf(Name)
        )
        var id: String? = null
        if (cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SP_ID))
        }
        cursor.close()
        return id
    }


    // 3. KHÁCH HÀNG
    // hiển thị
    fun getAllKH(): List<KhachHang> {
        val KHList = mutableListOf<KhachHang>()
        val select = "SELECT * FROM $TABLE_KHACHHANG"
        val db = this.readableDatabase
        val cursor = db.rawQuery(select, null)
        if (cursor.moveToFirst()) {
            do {
                val kh = KhachHang(
                    maKH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_ID)),
                    tenKH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_TEN)),
                    diaChi = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_DIACHI)),
                    ngaySinh = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_NGAYSINH)),
                    dienThoai = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_DIENTHOAI)),
                    email =  cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_EMAIL)),
                    thanhVien = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_TVIEN))
                )
                KHList.add(kh)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return KHList
    }

    // hiển thị kh theo id
    fun getKHByID(id: String): List<KhachHang> {
        val KHList = mutableListOf<KhachHang>()
        val select = "SELECT * FROM $TABLE_KHACHHANG WHERE $COLUMN_KH_ID = ? "
        val db = this.readableDatabase
        val cursor = db.rawQuery(select, arrayOf(id))
        if (cursor.moveToFirst()) {
            do {
                val kh = KhachHang(
                    maKH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_ID)),
                    tenKH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_TEN)),
                    diaChi = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_DIACHI)),
                    ngaySinh = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_NGAYSINH)),
                    dienThoai = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_DIENTHOAI)),
                    email =  cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_EMAIL)),
                    thanhVien = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_TVIEN))
                )
                KHList.add(kh)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return KHList
    }
    // hiển thị kh theo tên
    fun getKHByName(name: String): List<KhachHang> {
        val KHList = mutableListOf<KhachHang>()
        val select = "SELECT * FROM $TABLE_KHACHHANG WHERE $COLUMN_KH_TEN = ? "
        val db = this.readableDatabase
        val cursor = db.rawQuery(select, arrayOf(name))
        if (cursor.moveToFirst()) {
            do {
                val kh = KhachHang(
                    maKH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_ID)),
                    tenKH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_TEN)),
                    diaChi = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_DIACHI)),
                    ngaySinh = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_NGAYSINH)),
                    dienThoai = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_DIENTHOAI)),
                    email =  cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_EMAIL)),
                    thanhVien = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_TVIEN))
                )
                KHList.add(kh)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return KHList
    }

    // thêm
    fun addKH(id: String, name: String, dc: String, ns: String, dt: String, email: String, tvien: String) {
        val db = this.writableDatabase

        // Kiểm tra nếu kh_id đã tồn tại
        val cursor = db.rawQuery("SELECT ${DatabaseHelper.COLUMN_KH_ID} FROM ${DatabaseHelper.TABLE_KHACHHANG} WHERE ${DatabaseHelper.COLUMN_KH_ID} = ?", arrayOf(id))
        if (cursor.count > 0) {
            // Nếu tồn tại, cập nhật bản ghi hiện có
            val update = "UPDATE ${DatabaseHelper.TABLE_KHACHHANG} SET " +
                    "${DatabaseHelper.COLUMN_KH_TEN} = '$name', " +
                    "${DatabaseHelper.COLUMN_KH_DIACHI} = '$dc', " +
                    "${DatabaseHelper.COLUMN_KH_NGAYSINH} = '$ns', " +
                    "${DatabaseHelper.COLUMN_KH_DIENTHOAI} = '$dt', " +
                    "${DatabaseHelper.COLUMN_KH_EMAIL} = '$email', " +
                    "${DatabaseHelper.COLUMN_KH_TVIEN} = '$tvien' " +
                    "WHERE ${DatabaseHelper.COLUMN_KH_ID} = '$id'"
            db.execSQL(update)
        } else {
            // Nếu không tồn tại, thêm bản ghi mới
            val insert = "INSERT INTO ${DatabaseHelper.TABLE_KHACHHANG} " +
                    "(${DatabaseHelper.COLUMN_KH_ID}, ${DatabaseHelper.COLUMN_KH_TEN}, ${DatabaseHelper.COLUMN_KH_DIACHI}, " +
                    "${DatabaseHelper.COLUMN_KH_NGAYSINH}, ${DatabaseHelper.COLUMN_KH_DIENTHOAI}, ${DatabaseHelper.COLUMN_KH_EMAIL}, " +
                    "${DatabaseHelper.COLUMN_KH_TVIEN}) " +
                    "VALUES('$id', '$name', '$dc', '$ns', '$dt', '$email', '$tvien')"
            db.execSQL(insert)
        }
        cursor.close()
        db.close()
    }


    // sửa
    fun updateKH(id: String, name: String, dc: String, ns: String, dt: String, email :String, tvien: String) {
        val db = this.writableDatabase
        val update = "UPDATE ${DatabaseHelper.TABLE_KHACHHANG}" +
                " SET ${DatabaseHelper.COLUMN_KH_TEN} = '$name', " +
                "${DatabaseHelper.COLUMN_KH_DIACHI} = '$dc', " +
                "${DatabaseHelper.COLUMN_KH_NGAYSINH} = '$ns', " +
                "${DatabaseHelper.COLUMN_KH_DIENTHOAI} = '$dt', " +
                "${DatabaseHelper.COLUMN_KH_EMAIL} = '$email', " +
                "${DatabaseHelper.COLUMN_KH_TVIEN} = '$tvien' " +
                "WHERE ${DatabaseHelper.COLUMN_KH_ID} = '$id' "
        db?.execSQL(update)
        db.close()
    }

    // xóa
    fun deleteKH(id: String) {
        val db = this.writableDatabase
        val delete =
            "DELETE FROM ${DatabaseHelper.TABLE_KHACHHANG} WHERE ${DatabaseHelper.COLUMN_KH_ID} = '$id'"
        db?.execSQL(delete)
        db.close()
    }
    // tìm kiếm
    fun searchKH(query: String): List<KhachHang> {
        val KHList = mutableListOf<KhachHang>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_KHACHHANG WHERE $COLUMN_KH_TEN LIKE ? OR $COLUMN_KH_ID LIKE ? " +
                    "OR $COLUMN_KH_DIACHI LIKE ? OR $COLUMN_KH_NGAYSINH LIKE ? OR $COLUMN_KH_DIENTHOAI LIKE ? " +
                    "OR $COLUMN_KH_EMAIL LIKE ? OR $COLUMN_KH_TVIEN LIKE ?",
            arrayOf(
                "%$query%", "%$query%", "%$query%", "%$query%", "%$query%", "%$query%", "%$query%"
            )
        )
        if (cursor.moveToFirst()) {
            do {
                val kh = KhachHang(
                    maKH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_ID)),
                    tenKH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_TEN)),
                    diaChi = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_DIACHI)),
                    ngaySinh = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_NGAYSINH)),
                    dienThoai = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_DIENTHOAI)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_EMAIL)),
                    thanhVien = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_TVIEN))
                )
                KHList.add(kh)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return KHList
    }


    // lấy id từ tên khách
    fun getIDkhByName(Name: String): String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_KH_ID FROM $TABLE_KHACHHANG " +
                    "WHERE $COLUMN_KH_TEN = ?", arrayOf(Name)
        )
        var id: String? = null
        if (cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_ID))
        }
        cursor.close()
        return id
    }


    // 4. ĐƠN HÀNG
    // hiển thị
    fun getAllDH(): List<DonHang> {
        val DHList = mutableListOf<DonHang>()
        val select = "SELECT $COLUMN_DH_ID, $COLUMN_DH_NGAYDH, $COLUMN_DH_TRANGTHAI, " +
                "$TABLE_NHANVIEN.$COLUMN_NV_HO || ' ' || $TABLE_NHANVIEN.$COLUMN_NV_TEN AS nv_full_name, " +
                "$TABLE_KHACHHANG.$COLUMN_KH_TEN " +
                "FROM $TABLE_DONHANG " +
                "JOIN $TABLE_KHACHHANG ON $TABLE_DONHANG.$COLUMN_DH_IDKH = $TABLE_KHACHHANG.$COLUMN_KH_ID " +
                "JOIN $TABLE_NHANVIEN ON $TABLE_DONHANG.$COLUMN_DH_IDNV = $TABLE_NHANVIEN.$COLUMN_NV_ID"
        val db = this.readableDatabase
        val cursor = db.rawQuery(select, null)
        if (cursor.moveToFirst()) {
            do {
                val dh = DonHang(
                    maDH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DH_ID)),
                    ngayDH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DH_NGAYDH)),
                    trangThai = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DH_TRANGTHAI)),
                    NV = cursor.getString(cursor.getColumnIndexOrThrow("nv_full_name")),
                    KH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_TEN))
                )
                DHList.add(dh)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return DHList
    }

    // hiển thị đơn hàng qua id
    fun getDHByID(id: String): List<DonHang> {
        val DHList = mutableListOf<DonHang>()
        val select = "SELECT $COLUMN_DH_ID, $COLUMN_DH_NGAYDH, $COLUMN_DH_TRANGTHAI, " +
                "$TABLE_NHANVIEN.$COLUMN_NV_HO || ' ' || $TABLE_NHANVIEN.$COLUMN_NV_TEN AS nv_full_name, " +
                "$TABLE_KHACHHANG.$COLUMN_KH_TEN " +
                "FROM $TABLE_DONHANG " +
                "JOIN $TABLE_KHACHHANG ON $TABLE_DONHANG.$COLUMN_DH_IDKH = $TABLE_KHACHHANG.$COLUMN_KH_ID " +
                "JOIN $TABLE_NHANVIEN ON $TABLE_DONHANG.$COLUMN_DH_IDNV = $TABLE_NHANVIEN.$COLUMN_NV_ID " +
                "WHERE $COLUMN_DH_ID = ? "
        val db = this.readableDatabase
        val cursor = db.rawQuery(select, arrayOf(id))
        if (cursor.moveToFirst()) {
            do {
                val dh = DonHang(
                    maDH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DH_ID)),
                    ngayDH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DH_NGAYDH)),
                    trangThai = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DH_TRANGTHAI)),
                    NV = cursor.getString(cursor.getColumnIndexOrThrow("nv_full_name")),
                    KH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_TEN))
                )
                DHList.add(dh)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return DHList
    }

    // thêm
    fun addDH(id: String, ngaydh: String, tthai: String, nv: String, kh: String) {
        val db = this.writableDatabase

        // Kiểm tra nếu dh_id đã tồn tại
        val cursor = db.rawQuery("SELECT ${DatabaseHelper.COLUMN_DH_ID} FROM ${DatabaseHelper.TABLE_DONHANG} WHERE ${DatabaseHelper.COLUMN_DH_ID} = ?", arrayOf(id))
        if (cursor.count > 0) {
            // Nếu tồn tại, cập nhật bản ghi hiện có
            val update = "UPDATE ${DatabaseHelper.TABLE_DONHANG} SET " +
                    "${DatabaseHelper.COLUMN_DH_NGAYDH} = '$ngaydh', " +
                    "${DatabaseHelper.COLUMN_DH_TRANGTHAI} = '$tthai', " +
                    "${DatabaseHelper.COLUMN_DH_IDNV} = '${getIDnvByName(nv)}', " +
                    "${DatabaseHelper.COLUMN_DH_IDKH} = '${getIDkhByName(kh)}' " +
                    "WHERE ${DatabaseHelper.COLUMN_DH_ID} = '$id'"
            db.execSQL(update)
        } else {
            // Nếu không tồn tại, thêm bản ghi mới
            val insert = "INSERT INTO ${DatabaseHelper.TABLE_DONHANG} " +
                    "(${DatabaseHelper.COLUMN_DH_ID}, ${DatabaseHelper.COLUMN_DH_NGAYDH}, ${DatabaseHelper.COLUMN_DH_TRANGTHAI}, " +
                    "${DatabaseHelper.COLUMN_DH_IDNV}, ${DatabaseHelper.COLUMN_DH_IDKH}) " +
                    "VALUES('$id', '$ngaydh', '$tthai', '${getIDnvByName(nv)}', '${getIDkhByName(kh)}')"
            db.execSQL(insert)
        }
        cursor.close()
        db.close()
    }


    // sửa
    fun updateDH(id: String, ngaydh: String, tthai: String, nv: String, kh: String) {
        val db = this.writableDatabase
        val update = "UPDATE ${DatabaseHelper.TABLE_DONHANG}" +
                " SET ${DatabaseHelper.COLUMN_DH_NGAYDH} = '$ngaydh', " +
                "${DatabaseHelper.COLUMN_DH_TRANGTHAI} = '$tthai', " +
                "${DatabaseHelper.COLUMN_DH_IDNV} = '${getIDnvByName(nv)}', " +
                "${DatabaseHelper.COLUMN_DH_IDKH} = '${getIDkhByName(kh)}' " +
                "WHERE ${DatabaseHelper.COLUMN_DH_ID} = '$id' "
        db?.execSQL(update)
        db.close()
    }

    // xóa
    fun deleteDH(id: String) {
        val db = this.writableDatabase
        val delete =
            "DELETE FROM ${DatabaseHelper.TABLE_DONHANG} WHERE ${DatabaseHelper.COLUMN_DH_ID} = '$id' "
        db?.execSQL(delete)
        db.close()
    }

    // tìm kiếm
    fun searchDH(query: String): List<DonHang> {
        val DHList = mutableListOf<DonHang>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_DH_ID, $COLUMN_DH_NGAYDH, $COLUMN_DH_TRANGTHAI, " +
                    "$TABLE_NHANVIEN.$COLUMN_NV_HO || ' ' || $TABLE_NHANVIEN.$COLUMN_NV_TEN AS nv_full_name, " +
                    "$COLUMN_KH_TEN FROM $TABLE_DONHANG " +
                    "JOIN $TABLE_NHANVIEN ON $TABLE_NHANVIEN.$COLUMN_NV_ID = $TABLE_DONHANG.$COLUMN_DH_IDNV " +
                    "JOIN $TABLE_KHACHHANG ON $TABLE_KHACHHANG.$COLUMN_KH_ID = $TABLE_DONHANG.$COLUMN_DH_IDKH " +
                    "WHERE $COLUMN_DH_ID LIKE ? OR $COLUMN_DH_IDKH LIKE ? " +
                    "OR $COLUMN_DH_IDNV LIKE ? OR $COLUMN_DH_TRANGTHAI LIKE ? " +
                    "OR $TABLE_NHANVIEN.$COLUMN_NV_HO LIKE ? OR $TABLE_NHANVIEN.$COLUMN_NV_TEN LIKE ? " +
                    "OR $TABLE_KHACHHANG.$COLUMN_KH_TEN LIKE ?",
            arrayOf(
                "%$query%", "%$query%", "%$query%", "%$query%",
                "%$query%", "%$query%", "%$query%"
            )
        )
        if (cursor.moveToFirst()) {
            do {
                val dh = DonHang(
                    maDH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DH_ID)),
                    ngayDH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DH_NGAYDH)),
                    trangThai = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DH_TRANGTHAI)),
                    NV = cursor.getString(cursor.getColumnIndexOrThrow("nv_full_name")),
                    KH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_TEN))
                )
                DHList.add(dh)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return DHList
    }


    // lấy thông tin đơn hàng thông qua số hd
    fun getDHBySoHD(soHD: String): List<DonHang> {
        val DHList = mutableListOf<DonHang>()
        val select = """
        SELECT $COLUMN_DH_ID, $COLUMN_DH_NGAYDH, $COLUMN_DH_TRANGTHAI, 
                $TABLE_NHANVIEN.$COLUMN_NV_HO || ' ' || $TABLE_NHANVIEN.$COLUMN_NV_TEN AS nv_full_name, 
                $TABLE_KHACHHANG.$COLUMN_KH_TEN 
        FROM $TABLE_DONHANG 
        JOIN $TABLE_KHACHHANG ON $TABLE_DONHANG.$COLUMN_DH_IDKH = $TABLE_KHACHHANG.$COLUMN_KH_ID 
        JOIN $TABLE_NHANVIEN ON $TABLE_DONHANG.$COLUMN_DH_IDNV = $TABLE_NHANVIEN.$COLUMN_NV_ID
        WHERE $TABLE_DONHANG.$COLUMN_DH_ID IN (
            SELECT $COLUMN_HD_IDDH FROM $TABLE_HOADON WHERE $COLUMN_HD_ID = ?
        )
    """
        val db = this.readableDatabase
        val cursor = db.rawQuery(select, arrayOf(soHD))
        if (cursor.moveToFirst()) {
            do {
                val dh = DonHang(
                    maDH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DH_ID)),
                    ngayDH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DH_NGAYDH)),
                    trangThai = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DH_TRANGTHAI)),
                    NV = cursor.getString(cursor.getColumnIndexOrThrow("nv_full_name")),
                    KH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KH_TEN))
                )
                DHList.add(dh)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return DHList
    }


    // 5. CHI TIẾT ĐƠN HÀNG
    // hiển thị
    fun getAllCTDH(): List<CTDonHang> {
        val CTDHList = mutableListOf<CTDonHang>()
        val select = "SELECT $COLUMN_CTDH_IDDH, $COLUMN_SP_TEN, $COLUMN_CTDH_SL, " +
                "$COLUMN_CTDH_GIA, $COLUMN_CTDH_TIEN " +
                "FROM $TABLE_CT_DONHANG " +
                "JOIN $TABLE_SANPHAM ON $TABLE_CT_DONHANG.$COLUMN_CTDH_IDSP = $TABLE_SANPHAM.$COLUMN_SP_ID"
        val db = this.readableDatabase
        val cursor = db.rawQuery(select, null)
        if (cursor.moveToFirst()) {
            do {
                val ctdh = CTDonHang(
                    maDH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CTDH_IDDH)),
                    SP = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SP_TEN)),
                    soLuong = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CTDH_SL)),
                    giaBan = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CTDH_GIA)),
                    thanhTien = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CTDH_TIEN))
                )
                CTDHList.add(ctdh)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return CTDHList
    }

    // hiển thị chi tiết đơn hàng qua id
    fun getCTDHByID(iddh: String): List<CTDonHang> {
        val CTDHList = mutableListOf<CTDonHang>()
        val select = "SELECT $COLUMN_CTDH_IDDH, $COLUMN_SP_TEN, $COLUMN_CTDH_SL, " +
                "$COLUMN_CTDH_GIA, $COLUMN_CTDH_TIEN " +
                "FROM $TABLE_CT_DONHANG " +
                "JOIN $TABLE_SANPHAM ON $TABLE_CT_DONHANG.$COLUMN_CTDH_IDSP = $TABLE_SANPHAM.$COLUMN_SP_ID " +
                "WHERE $TABLE_CT_DONHANG.$COLUMN_CTDH_IDDH = ? "
        val db = this.readableDatabase
        val cursor = db.rawQuery(select, arrayOf(iddh))
        if (cursor.moveToFirst()) {
            do {
                val ctdh = CTDonHang(
                    maDH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CTDH_IDDH)),
                    SP = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SP_TEN)),
                    soLuong = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CTDH_SL)),
                    giaBan = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CTDH_GIA)),
                    thanhTien = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CTDH_TIEN))
                )
                CTDHList.add(ctdh)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return CTDHList
    }


    // thêm
    fun addCTDH(id: String, sp: String, sl: Int, gia: Double, tien: Double) {
        val db = this.writableDatabase

        // Kiểm tra nếu iddh và idsp đã tồn tại
        val cursor = db.rawQuery("SELECT ${DatabaseHelper.COLUMN_CTDH_IDDH}, ${DatabaseHelper.COLUMN_CTDH_IDSP} FROM ${DatabaseHelper.TABLE_CT_DONHANG} WHERE ${DatabaseHelper.COLUMN_CTDH_IDDH} = ? AND ${DatabaseHelper.COLUMN_CTDH_IDSP} = ?", arrayOf(id, getIDspByName(sp)))
        if (cursor.count > 0) {
            // Nếu tồn tại, cập nhật bản ghi hiện có
            val update = "UPDATE ${DatabaseHelper.TABLE_CT_DONHANG} SET " +
                    "${DatabaseHelper.COLUMN_CTDH_SL} = '$sl', " +
                    "${DatabaseHelper.COLUMN_CTDH_GIA} = '$gia', " +
                    "${DatabaseHelper.COLUMN_CTDH_TIEN} = '$tien' " +
                    "WHERE ${DatabaseHelper.COLUMN_CTDH_IDDH} = '$id' AND ${DatabaseHelper.COLUMN_CTDH_IDSP} = '${getIDspByName(sp)}'"
            db.execSQL(update)
        } else {
            // Nếu không tồn tại, thêm bản ghi mới
            val insert = "INSERT INTO ${DatabaseHelper.TABLE_CT_DONHANG} " +
                    "(${DatabaseHelper.COLUMN_CTDH_IDDH}, ${DatabaseHelper.COLUMN_CTDH_IDSP}, ${DatabaseHelper.COLUMN_CTDH_SL}, " +
                    "${DatabaseHelper.COLUMN_CTDH_GIA}, ${DatabaseHelper.COLUMN_CTDH_TIEN}) " +
                    "VALUES('$id', '${getIDspByName(sp)}', '$sl', '$gia', '$tien')"
            db.execSQL(insert)
        }
        cursor.close()
        db.close()
    }


    // sửa
    fun updateCTDH(id: String, sp: String, sl: Int, gia: Double, tien: Double) {
        val db = this.writableDatabase
        val update = "UPDATE ${DatabaseHelper.TABLE_CT_DONHANG} " +
                "SET ${DatabaseHelper.COLUMN_CTDH_SL} = '$sl', " +
                "${DatabaseHelper.COLUMN_CTDH_GIA} = '$gia', " +
                "${DatabaseHelper.COLUMN_CTDH_TIEN} = '$tien' " +
                "WHERE ${DatabaseHelper.COLUMN_CTDH_IDDH} = '$id' " +
                "AND ${DatabaseHelper.COLUMN_CTDH_IDSP} = '${getIDspByName(sp)}'"
        db?.execSQL(update)
        db.close()
    }
    // xóa
    fun deleteCTDH(id: String, sp: String) {
        val db = this.writableDatabase
        val delete = "DELETE FROM ${DatabaseHelper.TABLE_CT_DONHANG} " +
                "WHERE ${DatabaseHelper.COLUMN_CTDH_IDDH} = '$id' " +
                "AND ${DatabaseHelper.COLUMN_CTDH_IDSP} = '${getIDspByName(sp)}'"
        db?.execSQL(delete)
        db.close()
    }

    // tính tổng đơn hàng
    fun getTongDH(maDH: String): Double {
        val db = this.readableDatabase
        val query =
            "SELECT SUM($COLUMN_CTDH_TIEN) FROM $TABLE_CT_DONHANG WHERE $COLUMN_CTDH_IDDH = ?"
        val cursor = db.rawQuery(query, arrayOf(maDH))
        var tongHD = 0.0
        if (cursor.moveToFirst()) {
            tongHD = cursor.getDouble(0)
        }
        cursor.close()
        db.close()
        return tongHD
    }


    // 6. HÓA ĐƠN
    // hiển thị
    fun getHD(): List<HoaDon> {
        val HDList = mutableListOf<HoaDon>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_HD_ID, $COLUMN_HD_NGAYLAP, $COLUMN_HD_PTTT, $COLUMN_HD_IDDH FROM $TABLE_HOADON ",
           null
        )
        if (cursor.moveToFirst()) {
            do {
                val hd = HoaDon(
                    soHD = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HD_ID)),
                    ngayLap = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HD_NGAYLAP)),
                    PTTT = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HD_PTTT)),
                    maDH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HD_IDDH)),
                    // Thêm các cột không có trong câu truy vấn để giữ nguyên đối tượng HoaDon
                    phiVC = 0.0, // Hoặc giá trị mặc định phù hợp
                    giamGia = 0.0, // Hoặc giá trị mặc định phù hợp
                    tongHD = 0.0 // Hoặc giá trị mặc định phù hợp
                )
                HDList.add(hd)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return HDList
    }
    // hiển thị
    fun getAllHD(): List<HoaDon> {
        val HDList = mutableListOf<HoaDon>()
        val select = "SELECT * FROM $TABLE_HOADON "
        val db = this.readableDatabase
        val cursor = db.rawQuery(select, null)
        if (cursor.moveToFirst()) {
            do {
                val soHD = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HD_ID)) ?: ""
                val ngayLap = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HD_NGAYLAP)) ?: ""
                val PTTT = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HD_PTTT)) ?: ""
                val phiVC = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_HD_PHIVC))
                val giamGia = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_HD_GIAMGIA))
                val tongHD = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_HD_TONGHD))
                val maDH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HD_IDDH)) ?: ""
                val hd = HoaDon(soHD, ngayLap, PTTT, phiVC, giamGia, tongHD, maDH)
                HDList.add(hd)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return HDList
    }
    // hiển thị HD qua id
    fun getHDByID(id: String): List<HoaDon> {
        val HDList = mutableListOf<HoaDon>()
        val select = "SELECT * FROM $TABLE_HOADON WHERE $COLUMN_HD_ID = ? "
        val db = this.readableDatabase
        val cursor = db.rawQuery(select, arrayOf(id))
        if (cursor.moveToFirst()) {
            do {
                val soHD = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HD_ID)) ?: ""
                val ngayLap = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HD_NGAYLAP)) ?: ""
                val PTTT = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HD_PTTT)) ?: ""
                val phiVC = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_HD_PHIVC))
                val giamGia = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_HD_GIAMGIA))
                val tongHD = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_HD_TONGHD))
                val maDH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HD_IDDH)) ?: ""
                val hd = HoaDon(soHD, ngayLap, PTTT, phiVC, giamGia, tongHD, maDH)
                HDList.add(hd)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return HDList
    }
    // hiển thị qua mã đh
    fun getHDByIDDH(id: String): List<HoaDon> {
        val HDList = mutableListOf<HoaDon>()
        val select = "SELECT * FROM $TABLE_HOADON WHERE $COLUMN_HD_IDDH = ? "
        val db = this.readableDatabase
        val cursor = db.rawQuery(select, arrayOf(id))
        if (cursor.moveToFirst()) {
            do {
                val soHD = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HD_ID)) ?: ""
                val ngayLap = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HD_NGAYLAP)) ?: ""
                val PTTT = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HD_PTTT)) ?: ""
                val phiVC = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_HD_PHIVC))
                val giamGia = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_HD_GIAMGIA))
                val tongHD = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_HD_TONGHD))
                val maDH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HD_IDDH)) ?: ""
                val hd = HoaDon(soHD, ngayLap, PTTT, phiVC, giamGia, tongHD, maDH)
                HDList.add(hd)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        // Log the retrieved invoice details for debugging
        println("getHDByIDDH - Invoice Details: $HDList")

        return HDList
    }

    // thêm
    fun addHD(id: String, ngay: String, pttt: String, phivc: Double, giamgia: Double, tongHD: Double, iddh: String) {
        val db = this.writableDatabase

        // Kiểm tra nếu id đã tồn tại
        val cursor = db.rawQuery("SELECT ${DatabaseHelper.COLUMN_HD_ID} FROM ${DatabaseHelper.TABLE_HOADON} WHERE ${DatabaseHelper.COLUMN_HD_ID} = ?", arrayOf(id))
        if (cursor.count > 0) {
            // Nếu tồn tại, cập nhật bản ghi hiện có
            val update = "UPDATE ${DatabaseHelper.TABLE_HOADON} SET " +
                    "${DatabaseHelper.COLUMN_HD_NGAYLAP} = '$ngay', " +
                    "${DatabaseHelper.COLUMN_HD_PTTT} = '$pttt', " +
                    "${DatabaseHelper.COLUMN_HD_PHIVC} = '$phivc', " +
                    "${DatabaseHelper.COLUMN_HD_GIAMGIA} = '$giamgia', " +
                    "${DatabaseHelper.COLUMN_HD_TONGHD} = '$tongHD', " +
                    "${DatabaseHelper.COLUMN_HD_IDDH} = '$iddh' " +
                    "WHERE ${DatabaseHelper.COLUMN_HD_ID} = '$id'"
            db.execSQL(update)
        } else {
            // Nếu không tồn tại, thêm bản ghi mới
            val insert = "INSERT INTO ${DatabaseHelper.TABLE_HOADON} " +
                    "(${DatabaseHelper.COLUMN_HD_ID}, ${DatabaseHelper.COLUMN_HD_NGAYLAP}, " +
                    "${DatabaseHelper.COLUMN_HD_PTTT}, ${DatabaseHelper.COLUMN_HD_PHIVC}, " +
                    "${DatabaseHelper.COLUMN_HD_GIAMGIA}, ${DatabaseHelper.COLUMN_HD_TONGHD}, " +
                    "${DatabaseHelper.COLUMN_HD_IDDH}) " +
                    "VALUES('$id', '$ngay', '$pttt', '$phivc', '$giamgia', '$tongHD', '$iddh')"
            db.execSQL(insert)
        }
        cursor.close()
        db.close()
    }

    // sửa
    fun updateHD(id :String, ngay :String, pttt :String, phivc :Double, giamgia :Double, tongHD: Double, iddh :String)
    {
        val db = this.writableDatabase
        val update = "UPDATE ${DatabaseHelper.TABLE_HOADON}" +
                " SET ${DatabaseHelper.COLUMN_HD_NGAYLAP} = '$ngay', " +
                "${DatabaseHelper.COLUMN_HD_PTTT} = '$pttt', " +
                "${DatabaseHelper.COLUMN_HD_PHIVC} = '$phivc', " +
                "${DatabaseHelper.COLUMN_HD_GIAMGIA} = '$giamgia', " +
                "${DatabaseHelper.COLUMN_HD_TONGHD} = '$tongHD', " +
                "${DatabaseHelper.COLUMN_HD_IDDH} = '$iddh' " +
                "WHERE ${DatabaseHelper.COLUMN_HD_ID} = '$id' "
        db?.execSQL(update)
        db.close()
    }
    // xóa
    fun deleteHD(id :String)
    {
        val db = this.writableDatabase
        val delete = "DELETE FROM ${DatabaseHelper.TABLE_HOADON} WHERE ${DatabaseHelper.COLUMN_HD_ID} = '$id' "
        db?.execSQL(delete)
        db.close()
    }
    // tìm kiếm
    fun searchHD(query: String): List<HoaDon> {
        val HDList = mutableListOf<HoaDon>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_HD_ID, $COLUMN_HD_NGAYLAP, $COLUMN_HD_PTTT, $COLUMN_HD_IDDH FROM $TABLE_HOADON " +
                    "WHERE $COLUMN_HD_ID LIKE ? OR $COLUMN_HD_NGAYLAP LIKE ? " +
                    "OR $COLUMN_HD_PTTT LIKE ? " +
                    "OR $COLUMN_HD_IDDH LIKE ? ",
            arrayOf("%$query%", "%$query%", "%$query%", "%$query%")
        )
        if (cursor.moveToFirst()) {
            do {
                val hd = HoaDon(
                    soHD = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HD_ID)),
                    ngayLap = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HD_NGAYLAP)),
                    PTTT = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HD_PTTT)),
                    maDH = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HD_IDDH)),
                    // Thêm các cột không có trong câu truy vấn để giữ nguyên đối tượng HoaDon
                    phiVC = 0.0, // Hoặc giá trị mặc định phù hợp
                    giamGia = 0.0, // Hoặc giá trị mặc định phù hợp
                    tongHD = 0.0 // Hoặc giá trị mặc định phù hợp
                )
                HDList.add(hd)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return HDList
    }

    // xem số hóa đơn đã tồn tại hay chưa
    fun checkIfSoHDExists(soHD: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_HOADON} WHERE ${DatabaseHelper.COLUMN_HD_ID} = ? "
        val cursor = db.rawQuery(query, arrayOf(soHD))
        var exists = false
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0
        }
        cursor.close()
        db.close()
        return exists
    }
    //
    fun checkIfHDExistsForDH(maDH : String) : Boolean{
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_HOADON} WHERE ${DatabaseHelper.COLUMN_HD_IDDH} = ? "
        val cursor = db.rawQuery(query, arrayOf(maDH))
        var exists = false
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0
        }
        cursor.close()
        db.close()
        return exists
    }


    // 7. NHÂN VIÊN
    // hiển thị
    fun getAllNV() : List<NhanVien>
    {
        val NVList = mutableListOf<NhanVien>()
        val select = "SELECT * FROM $TABLE_NHANVIEN"
        val db = this.readableDatabase
        val cursor = db.rawQuery(select, null)
        if(cursor.moveToFirst())
        {
            do{
                val nv = NhanVien(
                    maNV = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_ID)),
                    hoNV = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_HO)),
                    tenNV = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_TEN)),
                    gioiTinh = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_GIOITINH)),
                    ngaySinh = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_NGAYSINH)),
                    diaChi = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_DIACHI)),
                    dienThoai = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_DIENTHOAI)),
                    noiSinh = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_NOISINH)),
                    ngayVL = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_NGAYVL)),
                    emailNV = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_EMAIL))
                )
                NVList.add(nv)
            } while(cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return NVList
    }
    // hiển thị by id
    fun getNVByID(id : String) : List<NhanVien>
    {
        val NVList = mutableListOf<NhanVien>()
        val select = "SELECT * FROM $TABLE_NHANVIEN WHERE $COLUMN_NV_ID = ? "
        val db = this.readableDatabase
        val cursor = db.rawQuery(select, arrayOf(id))
        if(cursor.moveToFirst())
        {
            do{
                val nv = NhanVien(
                    maNV = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_ID)),
                    hoNV = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_HO)),
                    tenNV = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_TEN)),
                    gioiTinh = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_GIOITINH)),
                    ngaySinh = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_NGAYSINH)),
                    diaChi = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_DIACHI)),
                    dienThoai = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_DIENTHOAI)),
                    noiSinh = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_NOISINH)),
                    ngayVL = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_NGAYVL)),
                    emailNV = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_EMAIL))
                )
                NVList.add(nv)
            } while(cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return NVList
    }
    // thêm
    fun addNV(id :String, ho :String, ten :String, gt :String, ngays :String, dc :String, dt :String, nois :String, nvl :String, email :String)
    {
        val db = this.writableDatabase
        val insert =  "INSERT INTO ${DatabaseHelper.TABLE_NHANVIEN}" +
                "(${DatabaseHelper.COLUMN_NV_ID}, ${DatabaseHelper.COLUMN_NV_HO}, ${DatabaseHelper.COLUMN_NV_TEN}, " +
                "${DatabaseHelper.COLUMN_NV_GIOITINH}, ${DatabaseHelper.COLUMN_NV_NGAYSINH}, ${DatabaseHelper.COLUMN_NV_DIACHI}, " +
                "${DatabaseHelper.COLUMN_NV_DIENTHOAI}, ${DatabaseHelper.COLUMN_NV_NOISINH}, ${DatabaseHelper.COLUMN_NV_NGAYVL}, " +
                "${DatabaseHelper.COLUMN_NV_EMAIL})" +
                "VALUES('$id', '$ho', '$ten', '$gt', '$ngays', '$dc', '$dt', '$nois', '$nvl', '$email')"
        db?.execSQL(insert)
        db.close()
    }
    // sửa
    fun updateNV(id :String, ho :String, ten :String, gt :String, ngays :String, dc :String, dt :String, nois :String, nvl :String, email :String)
    {
        val db = this.writableDatabase
        val update =  "UPDATE ${DatabaseHelper.TABLE_NHANVIEN}" +
                " SET ${DatabaseHelper.COLUMN_NV_HO} = '$ho', " +
                "${DatabaseHelper.COLUMN_NV_TEN} = '$ten', " +
                "${DatabaseHelper.COLUMN_NV_GIOITINH} = '$gt', " +
                "${DatabaseHelper.COLUMN_NV_NGAYSINH} = '$ngays', " +
                "${DatabaseHelper.COLUMN_NV_DIACHI} = '$dc', " +
                "${DatabaseHelper.COLUMN_NV_DIENTHOAI} = '$dt', " +
                "${DatabaseHelper.COLUMN_NV_NOISINH} = '$nois', " +
                "${DatabaseHelper.COLUMN_NV_NGAYVL} = '$nvl' , " +
                "${DatabaseHelper.COLUMN_NV_EMAIL} = '$email' " +
                "WHERE ${DatabaseHelper.COLUMN_NV_ID} = '$id'"
        db?.execSQL(update)
        db.close()
    }
    // xóa
    fun deleteNV(name: String): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NHANVIEN, "$COLUMN_NV_ID = ?", arrayOf(getIDnvByName(name)))
        db.close()
        return result > 0
    }

    // Lấy tên nhân viên
    fun getNameNV(): List<String> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NHANVIEN", null)
        val namenv = mutableListOf<String>()
        if (cursor.moveToFirst()) {
            do {
                val ho = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_HO))
                val ten = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_TEN))
                namenv.add("$ho $ten")
            } while (cursor.moveToNext())
        }
        cursor.close()
        return namenv
    }
    // lấy id từ tên nhân viên
    fun getIDnvByName(fullName: String): String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_NV_ID FROM $TABLE_NHANVIEN " +
                "WHERE $COLUMN_NV_HO || ' ' || $COLUMN_NV_TEN = ?", arrayOf(fullName))
        var id: String? = null
        if (cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_ID))
        }
        cursor.close()
        return id
    }
    // kiểm tra tồn tại id nv
    fun isIDnvExists(username: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_NHANVIEN} WHERE ${DatabaseHelper.COLUMN_NV_ID} = ?"
        val cursor = db.rawQuery(query, arrayOf(username))
        var exists = false
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0
        }
        cursor.close()
        return exists
    }


    // 8. TÀI KHOẢN
    // hiển thị
    fun getAllTK(): List<TaiKhoan> {
        val TKList = mutableListOf<TaiKhoan>()
        val select = "SELECT $COLUMN_TK_TENDN, $COLUMN_TK_MATKHAU, $COLUMN_NV_HO, $COLUMN_NV_TEN " +
                "FROM $TABLE_TAIKHOAN " +
                "JOIN $TABLE_NHANVIEN ON $TABLE_TAIKHOAN.$COLUMN_TK_IDNV = $TABLE_NHANVIEN.$COLUMN_NV_ID"
        val db = this.readableDatabase
        val cursor = db.rawQuery(select, null)
        if (cursor.moveToFirst()) {
            do {
                val tk = TaiKhoan(
                    tenDN = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TK_TENDN)),
                    matKhau = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TK_MATKHAU)),
                    nhanVien = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_HO)) + " " +
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_TEN))
                )
                TKList.add(tk)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return TKList
    }
    // lấy thông tin tài khoản dựa trên tên đăng nhập
    fun getTKByTenDN(username :String) : List<TaiKhoan>
    {
        val TKList = mutableListOf<TaiKhoan>()
        val select = "SELECT $COLUMN_TK_TENDN, $COLUMN_TK_MATKHAU, $COLUMN_NV_HO, $COLUMN_NV_TEN " +
                "FROM $TABLE_TAIKHOAN " +
                "JOIN $TABLE_NHANVIEN ON $TABLE_TAIKHOAN.$COLUMN_TK_IDNV = $TABLE_NHANVIEN.$COLUMN_NV_ID " +
                "WHERE $TABLE_TAIKHOAN.$COLUMN_TK_TENDN = ? "
        val db = this.readableDatabase
        val cursor = db.rawQuery(select, arrayOf(username))
        if (cursor.moveToFirst()) {
            do {
                val tk = TaiKhoan(
                    tenDN = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TK_TENDN)),
                    matKhau = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TK_MATKHAU)),
                    nhanVien = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_HO)) + " " +
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_TEN))
                )
                TKList.add(tk)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return TKList
    }
    // hiển thị tk = id
    fun getTKByID(id : String): List<TaiKhoan> {
        val TKList = mutableListOf<TaiKhoan>()
        val select = "SELECT $COLUMN_TK_TENDN, $COLUMN_TK_MATKHAU, $COLUMN_NV_HO, $COLUMN_NV_TEN " +
                "FROM $TABLE_TAIKHOAN " +
                "JOIN $TABLE_NHANVIEN ON $TABLE_TAIKHOAN.$COLUMN_TK_IDNV = $TABLE_NHANVIEN.$COLUMN_NV_ID " +
                "WHERE $TABLE_TAIKHOAN.$COLUMN_TK_IDNV = ? "
        val db = this.readableDatabase
        val cursor = db.rawQuery(select, arrayOf(id))
        if (cursor.moveToFirst()) {
            do {
                val tk = TaiKhoan(
                    tenDN = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TK_TENDN)),
                    matKhau = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TK_MATKHAU)),
                    nhanVien = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_HO)) + " " +
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_TEN))
                )
                TKList.add(tk)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return TKList
    }
    // lớp user detail
    data class UserDetails(
        val fullName: String,
        val email: String,
        val id : String
    )
    fun getUserDetails(username: String): UserDetails? {
        val db = this.readableDatabase
        val query = """
            SELECT nv.$COLUMN_NV_HO, nv.$COLUMN_NV_TEN, nv.$COLUMN_NV_EMAIL, nv.$COLUMN_NV_ID
            FROM $TABLE_NHANVIEN nv
            INNER JOIN $TABLE_TAIKHOAN tk ON nv.$COLUMN_NV_ID = tk.$COLUMN_TK_IDNV
            WHERE tk.$COLUMN_TK_TENDN = ?
        """
        val cursor = db.rawQuery(query, arrayOf(username))

        var userDetails: UserDetails? = null

        if (cursor.moveToFirst()) {
            val firstName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_HO))
            val lastName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_TEN))
            val fullName = "$firstName $lastName"
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_EMAIL))
            val idnv = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_ID))
            userDetails = UserDetails(fullName, email, idnv)
        }
        cursor.close()
        db.close()
        return userDetails
    }
    // thêm
    fun addTK(user :String, pass :String, nv :String)
    {
        val db = this.writableDatabase
        val insert = "INSERT INTO ${DatabaseHelper.TABLE_TAIKHOAN}" +
                "(${DatabaseHelper.COLUMN_TK_TENDN}, ${DatabaseHelper.COLUMN_TK_MATKHAU}, ${DatabaseHelper.COLUMN_TK_IDNV})" +
                " VALUES('$user', '$pass', '${getIDnvByName(nv)}') "
        db?.execSQL(insert)
        db.close()
    }
    // Hàm cập nhật mật khẩu người dùng dựa trên tên đăng nhập
    fun updateTK(user: String, newpass: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TK_MATKHAU, newpass)
        }
        val rowsUpdated = db.update(
            DatabaseHelper.TABLE_TAIKHOAN,
            values,
            "${DatabaseHelper.COLUMN_TK_TENDN} = ?",
            arrayOf(user)
        )
        db.close()
        return rowsUpdated > 0
    }
    // xóa tk
    fun deleteTK(username: String): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_TAIKHOAN, "$COLUMN_TK_TENDN = ?", arrayOf(username))
        db.close()
        return result > 0
    }
    // kiểm tra tồn tại tên đn
    fun isUsernameExists(username: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_TAIKHOAN} WHERE ${DatabaseHelper.COLUMN_TK_TENDN} = ?"
        val cursor = db.rawQuery(query, arrayOf(username))
        var exists = false
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0
        }
        cursor.close()
        return exists
    }
    // check đăng nhập
    fun checkLogin(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_TAIKHOAN} " +
                "WHERE ${DatabaseHelper.COLUMN_TK_TENDN} = ? AND ${DatabaseHelper.COLUMN_TK_MATKHAU} = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))
        var exists = false
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0
        }
        cursor.close()
        return exists
    }
    // lấy email từ user name
    fun getEmailByUsername(username: String): String? {
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_NV_EMAIL FROM $TABLE_NHANVIEN JOIN $TABLE_TAIKHOAN " +
                "ON $TABLE_NHANVIEN.$COLUMN_NV_ID = $TABLE_TAIKHOAN.$COLUMN_TK_IDNV WHERE $COLUMN_TK_TENDN = ?"
        val cursor = db.rawQuery(query, arrayOf(username))
        var email: String? = null
        if (cursor.moveToFirst()) {
            email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_EMAIL))
        }
        cursor.close()
        return email
    }
    // cập nhật mk mới
    fun updateUserPassword(email: String, newPassword: String): Boolean {
        val db = this.writableDatabase

        // Lấy ID của nhân viên dựa trên email
        val query = """
        SELECT $COLUMN_NV_ID 
        FROM $TABLE_NHANVIEN 
        WHERE $COLUMN_NV_EMAIL = ?
    """
        val cursor = db.rawQuery(query, arrayOf(email))
        val nvId: String? = if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NV_ID))
        } else {
            cursor.close()
            return false // Email không tồn tại
        }
        cursor.close()

        // Cập nhật mật khẩu dựa trên ID nhân viên
        val contentValues = ContentValues().apply {
            put(COLUMN_TK_MATKHAU, newPassword)
        }
        val result = db.update(TABLE_TAIKHOAN, contentValues, "$COLUMN_TK_IDNV = ?", arrayOf(nvId))
        return result > 0
    }

    // THỐNG KÊ TÌNH HÌNH KINH DOANH THEO NGÀY/ THÁNG/ QUÝ/ NĂM
    fun convertDateFormat(date: String, fromFormat: String, toFormat: String): String {
        val sdf = SimpleDateFormat(fromFormat, Locale.getDefault())
        val parsedDate = sdf.parse(date)
        val sdf2 = SimpleDateFormat(toFormat, Locale.getDefault())
        return sdf2.format(parsedDate)
    }

    fun getStatisticsByDay(tuNgay: String, denNgay: String): List<Pair<String, Statistic>> {
        val db = this.readableDatabase
        val formattedTuNgay = convertDateFormat(tuNgay, "dd/MM/yyyy", "yyyy-MM-dd")
        val formattedDenNgay = convertDateFormat(denNgay, "dd/MM/yyyy", "yyyy-MM-dd")
        val query = """
        SELECT dh_ngaydh, COUNT(DISTINCT dh_idkh) AS khach_hang, SUM(ct.ctdh_soluong) AS san_pham
        FROM $TABLE_DONHANG dh
        JOIN $TABLE_CT_DONHANG ct ON dh.$COLUMN_DH_ID = ct.$COLUMN_CTDH_IDDH
        WHERE strftime('%Y-%m-%d', substr(dh.$COLUMN_DH_NGAYDH, 7, 4) || '-' || substr(dh.$COLUMN_DH_NGAYDH, 4, 2) || '-' || substr(dh.$COLUMN_DH_NGAYDH, 1, 2))
              BETWEEN ? AND ?
        GROUP BY dh.$COLUMN_DH_NGAYDH
    """
        val cursor = db.rawQuery(query, arrayOf(formattedTuNgay, formattedDenNgay))
        val statistics = mutableListOf<Pair<String, Statistic>>()
        if (cursor.moveToFirst()) {
            do {
                val date = cursor.getString(cursor.getColumnIndexOrThrow("dh_ngaydh"))
                val khachHang = cursor.getInt(cursor.getColumnIndexOrThrow("khach_hang"))
                val sanPham = cursor.getInt(cursor.getColumnIndexOrThrow("san_pham"))
                statistics.add(date to Statistic(khachHang, sanPham))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return statistics
    }

    fun getStatisticsByMonth(tuNgay: String, denNgay: String): List<Pair<String, Statistic>> {
        val db = this.readableDatabase
        val formattedTuNgay = convertDateFormat(tuNgay, "dd/MM/yyyy", "yyyy-MM-dd")
        val formattedDenNgay = convertDateFormat(denNgay, "dd/MM/yyyy", "yyyy-MM-dd")
        val query = """
        SELECT (substr(dh.$COLUMN_DH_NGAYDH, 4, 2) || '/' || substr(dh.$COLUMN_DH_NGAYDH, 7, 4)) AS month, COUNT(DISTINCT dh_idkh) AS khach_hang, SUM(ct.ctdh_soluong) AS san_pham
        FROM $TABLE_DONHANG dh
        JOIN $TABLE_CT_DONHANG ct ON dh.$COLUMN_DH_ID = ct.$COLUMN_CTDH_IDDH
        WHERE strftime('%Y-%m-%d', substr(dh.$COLUMN_DH_NGAYDH, 7, 4) || '-' || substr(dh.$COLUMN_DH_NGAYDH, 4, 2) || '-' || substr(dh.$COLUMN_DH_NGAYDH, 1, 2))
              BETWEEN ? AND ?
        GROUP BY month
        ORDER BY month
    """
        val cursor = db.rawQuery(query, arrayOf(formattedTuNgay, formattedDenNgay))
        val statistics = mutableListOf<Pair<String, Statistic>>()
        if (cursor.moveToFirst()) {
            do {
                val date = cursor.getString(cursor.getColumnIndexOrThrow("month"))
                val khachHang = cursor.getInt(cursor.getColumnIndexOrThrow("khach_hang"))
                val sanPham = cursor.getInt(cursor.getColumnIndexOrThrow("san_pham"))
                statistics.add(date to Statistic(khachHang, sanPham))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return statistics
    }

    fun getStatisticsByQuarter(tuNgay: String, denNgay: String): List<Pair<String, Statistic>> {
        val db = this.readableDatabase
        val formattedTuNgay = convertDateFormat(tuNgay, "dd/MM/yyyy", "yyyy-MM-dd")
        val formattedDenNgay = convertDateFormat(denNgay, "dd/MM/yyyy", "yyyy-MM-dd")
        val query = """
        SELECT
            CASE 
                WHEN SUBSTR($COLUMN_DH_NGAYDH, 4, 2) IN ('01', '02', '03') THEN 'Q1/' || substr(dh.$COLUMN_DH_NGAYDH, 7, 4)
                WHEN SUBSTR($COLUMN_DH_NGAYDH, 4, 2) IN ('04', '05', '06') THEN 'Q2/' || substr(dh.$COLUMN_DH_NGAYDH, 7, 4)
                WHEN SUBSTR($COLUMN_DH_NGAYDH, 4, 2) IN ('07', '08', '09') THEN 'Q3/' || substr(dh.$COLUMN_DH_NGAYDH, 7, 4)
                ELSE 'Q4/' || substr(dh.$COLUMN_DH_NGAYDH, 7, 4)
            END AS quarter, 
            COUNT(DISTINCT dh_idkh) AS khach_hang, SUM(ct.ctdh_soluong) AS san_pham
        FROM $TABLE_DONHANG dh
        JOIN $TABLE_CT_DONHANG ct ON dh.$COLUMN_DH_ID = ct.$COLUMN_CTDH_IDDH
        WHERE strftime('%Y-%m-%d', substr(dh.$COLUMN_DH_NGAYDH, 7, 4) || '-' || substr(dh.$COLUMN_DH_NGAYDH, 4, 2) || '-' || substr(dh.$COLUMN_DH_NGAYDH, 1, 2))
              BETWEEN ? AND ?
        GROUP BY quarter
        ORDER BY quarter
    """
        val cursor = db.rawQuery(query, arrayOf(formattedTuNgay, formattedDenNgay))
        val statistics = mutableListOf<Pair<String, Statistic>>()
        if (cursor.moveToFirst()) {
            do {
                val date = cursor.getString(cursor.getColumnIndexOrThrow("quarter"))
                val khachHang = cursor.getInt(cursor.getColumnIndexOrThrow("khach_hang"))
                val sanPham = cursor.getInt(cursor.getColumnIndexOrThrow("san_pham"))
                statistics.add(date to Statistic(khachHang, sanPham))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return statistics
    }

    fun getStatisticsByYear(tuNgay: String, denNgay: String): List<Pair<String, Statistic>> {
        val db = this.readableDatabase
        val formattedTuNgay = convertDateFormat(tuNgay, "dd/MM/yyyy", "yyyy-MM-dd")
        val formattedDenNgay = convertDateFormat(denNgay, "dd/MM/yyyy", "yyyy-MM-dd")
        val query = """
        SELECT substr(dh.$COLUMN_DH_NGAYDH, 7, 4) AS year, 
            COUNT(DISTINCT dh_idkh) AS khach_hang, SUM(ct.ctdh_soluong) AS san_pham
        FROM $TABLE_DONHANG dh
        JOIN $TABLE_CT_DONHANG ct ON dh.$COLUMN_DH_ID = ct.$COLUMN_CTDH_IDDH
        WHERE strftime('%Y-%m-%d', substr(dh.$COLUMN_DH_NGAYDH, 7, 4) || '-' || substr(dh.$COLUMN_DH_NGAYDH, 4, 2) || '-' || substr(dh.$COLUMN_DH_NGAYDH, 1, 2))
              BETWEEN ? AND ?
        GROUP BY year
        ORDER BY year
    """
        val cursor = db.rawQuery(query, arrayOf(formattedTuNgay, formattedDenNgay))
        val statistics = mutableListOf<Pair<String, Statistic>>()
        if (cursor.moveToFirst()) {
            do {
                val date = cursor.getString(cursor.getColumnIndexOrThrow("year"))
                val khachHang = cursor.getInt(cursor.getColumnIndexOrThrow("khach_hang"))
                val sanPham = cursor.getInt(cursor.getColumnIndexOrThrow("san_pham"))
                statistics.add(date to Statistic(khachHang, sanPham))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return statistics
    }

    // THỐNG KÊ TÌNH TRẠNG ĐƠN HÀNG THEO NGÀY
    fun getTTDHByDate(date: String): Map<String, Int> {
        val db = this.readableDatabase
        val statistics = mutableMapOf<String, Int>()

        // Các trạng thái đơn hàng
        val statuses = listOf("Đặt hàng thành công", "Đã giao hàng", "Giao hàng thành công")

        // Chuyển đổi định dạng ngày
        val formattedDate = convertDateFormat(date, "dd/MM/yyyy", "yyyy-MM-dd")

        statuses.forEach { status ->
            statistics[status] = 0
        }

        // Truy vấn số lượng đơn hàng cho mỗi trạng thái trong ngày
        statuses.forEach { status ->
            val query = """
            SELECT COUNT(*) 
            FROM $TABLE_DONHANG 
            WHERE strftime('%Y-%m-%d', substr($COLUMN_DH_NGAYDH, 7, 4) || '-' || substr($COLUMN_DH_NGAYDH, 4, 2) || '-' || substr($COLUMN_DH_NGAYDH, 1, 2)) = ?
            AND $COLUMN_DH_TRANGTHAI = ?
            """
            val cursor = db.rawQuery(query, arrayOf(formattedDate, status))
            if (cursor.moveToFirst()) {
                statistics[status] = cursor.getInt(0)
            }
            cursor.close()
        }

        db.close()
        return statistics
    }
}