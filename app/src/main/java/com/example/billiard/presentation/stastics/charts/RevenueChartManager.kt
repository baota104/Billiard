package com.example.billiard.presentation.stastics.charts

import android.content.Context
import android.graphics.Color
import com.example.billiard.R
import com.example.billiard.domain.model.RevenueChart
import com.example.billiard.presentation.stastics.components.RevenueMarker
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class RevenueChartManager(private val context: Context) {

    fun setupChart(chart: LineChart) {
        chart.description.isEnabled = false
        chart.axisRight.isEnabled = false
        chart.setDrawGridBackground(false)

        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.granularity = 1f
        chart.xAxis.axisMinimum = 0f

        // 🛠️ FIX 1: Tăng khoảng trống bên dưới biểu đồ (Bottom Offset) để nhường chỗ cho Chú thích
        chart.setExtraOffsets(10f, 10f, 10f, 25f)

        // 🛠️ FIX 2: Căn chỉnh lại Chú thích (Legend) cho rộng rãi
        chart.legend.apply {
            textSize = 12f
            formToTextSpace = 8f
            xEntrySpace = 16f
            yOffset = 10f // Đẩy chú thích xuống thấp hơn trục X
            setDrawInside(false)
        }

        val marker = RevenueMarker(context, R.layout.chart_marker)
        marker.chartView = chart
        chart.marker = marker
    }

    fun updateChartData(chart: LineChart, data: RevenueChart) {
        val rawLabels = if (data.actual.isNotEmpty()) {
            data.actual.map { it.dateLabel }
        } else {
            data.predict.map { it.dateLabel }
        }

        // 🛠️ FIX 3: Rút gọn ngày tháng từ "2026-04-12" thành "12/04"
        val formattedLabels = rawLabels.map { dateStr ->
            try {
                if (dateStr.length >= 10 && dateStr.contains("-")) {
                    val parts = dateStr.take(10).split("-")
                    "${parts[2]}/${parts[1]}" // Lấy Ngày/Tháng (dd/MM)
                } else {
                    dateStr
                }
            } catch (e: Exception) {
                dateStr
            }
        }

        chart.xAxis.valueFormatter = IndexAxisValueFormatter(formattedLabels)

        val actualEntries = data.actual.mapIndexed { index, point ->
            Entry(index.toFloat(), point.revenue.toFloat())
        }

        val predictedEntries = data.predict.mapIndexed { index, point ->
            Entry(index.toFloat(), point.revenue.toFloat())
        }

        renderChart(chart, actualEntries, predictedEntries)
    }

    private fun renderChart(chart: LineChart, actual: List<Entry>, predicted: List<Entry>) {
        val actualSet = LineDataSet(actual, "Doanh thu thực tế").apply {
            color = Color.BLUE
            setCircleColor(Color.BLUE)
            lineWidth = 3f
            setDrawCircleHole(false)

            // 🛠️ FIX 4: Ẩn các con số chen chúc trên đường vẽ (Người dùng bấm vào điểm sẽ hiện Marker đẹp hơn)
            setDrawValues(false)
        }

        val predictSet = LineDataSet(predicted, "Dự đoán AI").apply {
            color = Color.parseColor("#7F00FF")
            setCircleColor(Color.parseColor("#7F00FF"))
            lineWidth = 3f
            enableDashedLine(10f, 5f, 0f)
            setDrawCircleHole(false)

            // 🛠️ FIX 4: Tương tự, ẩn các con số chen chúc
            setDrawValues(false)
        }

        chart.data = LineData(actualSet, predictSet)
        chart.animateX(800)
        chart.invalidate()
    }
}