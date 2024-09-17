package com.example.qlhoadon.QuanLy

import android.content.Context
import android.database.Cursor
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Spinner
import androidx.core.content.ContextCompat
import com.example.qlhoadon.DatabaseHelper
import com.example.qlhoadon.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ThongKeTHKDView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var spinner: Spinner
    private lateinit var edtTuNgay: EditText
    private lateinit var edtDenNgay: EditText
    private lateinit var btnOK: Button
    private lateinit var barChart: BarChart
    private lateinit var databaseHelper: DatabaseHelper

    init {
        LayoutInflater.from(context).inflate(R.layout.view_thongke_thkd, this, true)

        databaseHelper = DatabaseHelper(context)
        barChart = findViewById(R.id.BarChartTHKD)
        spinner = findViewById(R.id.spinnerTHKD)
        edtTuNgay = findViewById(R.id.edtTuNgay)
        edtDenNgay = findViewById(R.id.edtDenNgay)
        btnOK = findViewById(R.id.btnOK)

        // Thiết lập Spinner
        val options = arrayOf("Theo ngày", "Theo tháng", "Theo quý", "Theo năm")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        btnOK.setOnClickListener {
            val tuNgay = edtTuNgay.text.toString()
            val denNgay = edtDenNgay.text.toString()
            val option = spinner.selectedItem.toString()

            val statistics = when (option) {
                "Theo ngày" -> databaseHelper.getStatisticsByDay(tuNgay, denNgay)
                "Theo tháng" -> databaseHelper.getStatisticsByMonth(tuNgay, denNgay)
                "Theo quý" -> databaseHelper.getStatisticsByQuarter(tuNgay, denNgay)
                "Theo năm" -> databaseHelper.getStatisticsByYear(tuNgay, denNgay)
                else -> emptyList()
            }

            val khachHangEntries = statistics.mapIndexed { index, entry ->
                BarEntry(index.toFloat(), entry.second.khachHang.toFloat())
            }

            val sanPhamEntries = statistics.mapIndexed { index, entry ->
                BarEntry(index.toFloat(), entry.second.sanPham.toFloat())
            }

            val khachHangDataSet = BarDataSet(khachHangEntries, "Lượng khách mua").apply {
                color = ContextCompat.getColor(context, R.color.purple_200)
                valueTextSize = 11f
            }

            val sanPhamDataSet = BarDataSet(sanPhamEntries, "Lượng sản phẩm bán").apply {
                color = ContextCompat.getColor(context, R.color.teal_200)
                valueTextSize = 11f
            }

            val barData = BarData(khachHangDataSet, sanPhamDataSet).apply {
                barWidth = 0.45f
            }

            val groupSpace = 0.1f
            val barSpace = 0.05f
            val barWidth = 0.35f

            barChart.data = barData
            barChart.barData.barWidth = barWidth
            barChart.groupBars(0f, groupSpace, barSpace)
            barChart.description.isEnabled = false

            barChart.xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(statistics.map { it.first })
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
            }

            barChart.invalidate()
        }

    }
}