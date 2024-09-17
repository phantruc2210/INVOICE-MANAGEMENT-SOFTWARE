package com.example.qlhoadon.QuanLy

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import com.example.qlhoadon.DatabaseHelper
import com.example.qlhoadon.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class ThongKeTTDHView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var DateEditText: EditText
    private lateinit var applyButton: Button
    private lateinit var pieChart: PieChart

    init {
        LayoutInflater.from(context).inflate(R.layout.view_thongke_ttdh, this, true)
        databaseHelper = DatabaseHelper(context)
        DateEditText = findViewById(R.id.editTextDate)
        applyButton = findViewById(R.id.applyButton)
        pieChart = findViewById(R.id.pieChart)

        applyButton.setOnClickListener {
            val date = DateEditText.text.toString()
            updateChart(date)
        }
    }

    private fun updateChart(date: String) {
        val statistics = databaseHelper.getTTDHByDate(date)

        // Kiểm tra xem tất cả các giá trị có bằng 0 không
        val allZero = statistics.values.all { it == 0 }

        if (allZero) {
            // Hiển thị thông báo khi không có đơn hàng
            Toast.makeText(context, "Không có đơn hàng nào", Toast.LENGTH_SHORT).show()
            pieChart.visibility = View.VISIBLE

            val entries = ArrayList<PieEntry>()
            entries.add(PieEntry(1f, "Không có đơn hàng"))

            val dataSet = PieDataSet(entries, "")
            dataSet.colors = listOf(Color.GRAY)
            dataSet.valueTextColor = Color.TRANSPARENT
            dataSet.valueTextSize = 16f

            val pieData = PieData(dataSet)

            pieChart.data = pieData
            pieChart.centerText = "Không có\nđơn hàng"
            pieChart.setCenterTextSize(22f)
            pieChart.setCenterTextColor(Color.RED)
            pieChart.description.isEnabled = false
            pieChart.legend.isEnabled = false
            pieChart.holeRadius = 50f
            pieChart.transparentCircleRadius = 55f
            pieChart.isDrawHoleEnabled = true
            pieChart.setEntryLabelColor(Color.TRANSPARENT)

            pieChart.invalidate()
        } else {
            pieChart.visibility = View.VISIBLE

            // Tạo các mục cho biểu đồ, bao gồm cả các trạng thái có giá trị 0
            val entries = ArrayList<PieEntry>()
            val statuses = listOf("Đặt hàng thành công", "Đã giao hàng", "Giao hàng thành công")
            val colors = mutableListOf<Int>()

            val zeroValueStatuses = mutableListOf<String>()

            statuses.forEachIndexed { index, status ->
                val count = statistics[status]?.toFloat() ?: 0f
                if (count > 0) {
                    entries.add(PieEntry(count, status))
                    // Add color
                    colors.add(when (status) {
                        "Đặt hàng thành công" -> Color.CYAN
                        "Đã giao hàng" -> Color.GREEN
                        "Giao hàng thành công" -> Color.RED
                        else -> Color.GRAY
                    })
                } else {
                    val color = when (status) {
                        "Đặt hàng thành công" -> Color.CYAN
                        "Đã giao hàng" -> Color.GREEN
                        "Giao hàng thành công" -> Color.RED
                        else -> Color.GRAY
                    }
                    zeroValueStatuses.add(createDescriptionLine(status, color))
                }
            }

            val dataSet = PieDataSet(entries, "Trạng thái đơn hàng")
            dataSet.colors = colors
            dataSet.valueTextColor = Color.BLACK
            dataSet.valueTextSize = 16f
            dataSet.sliceSpace = 3f // Tăng khoảng cách giữa các mục

            val pieData = PieData(dataSet)

            pieChart.data = pieData
            pieChart.centerText = "Trạng thái\nđơn hàng"
            pieChart.setCenterTextSize(18f)
            pieChart.setCenterTextColor(Color.BLUE)
            pieChart.description.isEnabled = true
            pieChart.description.text = if (zeroValueStatuses.isNotEmpty()) {
                "\n${zeroValueStatuses.joinToString("\n")}"
            } else {
                ""
            }
            pieChart.description.textSize = 12f
            pieChart.legend.isEnabled = false
            pieChart.holeRadius = 30f
            pieChart.transparentCircleRadius = 35f
            pieChart.isDrawHoleEnabled = true
            pieChart.setEntryLabelColor(Color.BLACK)

            pieChart.invalidate()
        }
    }

    private fun createDescriptionLine(status: String, color: Int): String {
        return "• ${getColorIndicator(color)} ${status}: 0"
    }

    private fun getColorIndicator(color: Int): String {
        return when (color) {
            Color.CYAN -> "🔵"
            Color.GREEN -> "🟢"
            Color.RED -> "🔴"
            else -> "⚪"
        }
    }
}
