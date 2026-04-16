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
        chart.legend.textSize = 12f

        val marker = RevenueMarker(context, R.layout.chart_marker)
        marker.chartView = chart
        chart.marker = marker
    }

    // Tự động map dữ liệu từ Model RevenueChart vào Biểu đồ
    fun updateChartData(chart: LineChart, data: RevenueChart) {

        // 1. Lấy danh sách nhãn (labels) trục X.
        // Ưu tiên lấy từ actual, nếu actual rỗng thì lấy từ predict
        val labels = if (data.actual.isNotEmpty()) {
            data.actual.map { it.dateLabel }
        } else {
            data.predict.map { it.dateLabel }
        }
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        // 2. Map dữ liệu Thực tế (Actual)
        val actualEntries = data.actual.mapIndexed { index, point ->
            Entry(index.toFloat(), point.revenue.toFloat())
        }

        // 3. Map dữ liệu Dự đoán AI (Predict)
        val predictedEntries = data.predict.mapIndexed { index, point ->
            Entry(index.toFloat(), point.revenue.toFloat())
        }

        // 4. Gọi hàm vẽ biểu đồ
        renderChart(chart, actualEntries, predictedEntries)
    }
    private fun renderChart(chart: LineChart, actual: List<Entry>, predicted: List<Entry>) {
        val actualSet = LineDataSet(actual, "Doanh thu thực tế").apply {
            color = Color.BLUE
            setCircleColor(Color.BLUE)
            lineWidth = 3f
            setDrawCircleHole(false)
        }

        val predictSet = LineDataSet(predicted, "Dự đoán AI").apply {
            color = Color.parseColor("#7F00FF")
            setCircleColor(Color.parseColor("#7F00FF"))
            lineWidth = 3f
            enableDashedLine(10f, 5f, 0f)
            setDrawCircleHole(false)
        }

        chart.data = LineData(actualSet, predictSet)
        chart.animateX(800)
        chart.invalidate()
    }
}