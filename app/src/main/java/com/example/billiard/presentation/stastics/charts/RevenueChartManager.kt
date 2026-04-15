package com.example.billiard.presentation.stastics.charts

import android.content.Context
import android.graphics.Color
import android.view.View
import com.example.billiard.R
import com.example.billiard.presentation.stastics.components.RevenueMarker
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.button.MaterialButton

// Class quản lý vẽ biểu đồ
class RevenueChartManager(private val context: Context) {
    fun setupChart(chart: LineChart) {
//        Tắt desc, trục Y bên phải, lưới nền
        chart.description.isEnabled = false
        chart.axisRight.isEnabled = false
        chart.setDrawGridBackground(false)

//        Set trục X ở dưới, bước nhảy = 1, min = 0
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.granularity = 1f
        chart.xAxis.axisMinimum = 0f

        chart.legend.textSize = 12f

//        Setup custom marker view
        val marker = RevenueMarker(context, R.layout.chart_marker)
        marker.chartView = chart
        chart.marker = marker
    }

    fun loadWeekData(chart: LineChart) {
        val labels = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

//        TODO: Thay bằng dữ liệu lấy từ API
        val actualEntries = listOf(
            Entry(0f, 1200f),
            Entry(1f, 1500f),
            Entry(2f, 900f),
            Entry(3f, 1700f),
            Entry(4f, 2100f),
            Entry(5f, 2300f),
            Entry(6f, 1800f)
        )

        val predictedEntries = listOf(
            Entry(0f, 1100f),
            Entry(1f, 1400f),
            Entry(2f, 1000f),
            Entry(3f, 1600f),
            Entry(4f, 2000f),
            Entry(5f, 2400f),
            Entry(6f, 1900f)
        )

        renderChart(chart, actualEntries, predictedEntries)
    }

    fun loadYearData(chart: LineChart) {
        //TODO: Thay bằng dữ liệu lấy từ API
        val labels = listOf("Quý 1", "Quý 2", "Quý 3", "Quý 4")
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        val actualEntries = listOf(
            Entry(0f, 900f),
            Entry(1f, 1700f),
            Entry(2f, 1200f),
            Entry(3f, 1500f),
        )

        val predictedEntries = listOf(
            Entry(0f, 1000f),
            Entry(1f, 1600f),
            Entry(2f, 1100f),
            Entry(3f, 1400f),
        )

        renderChart(chart, actualEntries, predictedEntries)
    }

    fun loadMonthData(chart: LineChart) {
        //TODO: Thay bằng dữ liệu lấy từ API
        val labels = listOf("Tuần 1", "Tuần 2", "Tuần 3", "Tuần 4")
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        val actualEntries = listOf(
            Entry(0f, 1200f),
            Entry(1f, 1500f),
            Entry(2f, 900f),
            Entry(3f, 1700f),
        )

        val predictedEntries = listOf(
            Entry(0f, 1100f),
            Entry(1f, 1400f),
            Entry(2f, 1000f),
            Entry(3f, 1600f),
        )

        renderChart(chart, actualEntries, predictedEntries)
    }

    fun loadAllData(chart: LineChart) {
        //TODO: Thay bằng dữ liệu lấy từ API
        val labels = listOf("2020", "2021", "2022", "2023", "2024", "2025", "2026")
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        val actualEntries = listOf(
            Entry(0f, 1200f),
            Entry(1f, 900f),
            Entry(2f, 1700f),
            Entry(3f, 1700f),
            Entry(4f, 1500f),
            Entry(5f, 900f),
            Entry(6f, 1700f),
        )

        val predictedEntries = listOf(
            Entry(0f, 1400f),
            Entry(1f, 1000f),
            Entry(2f, 1000f),
            Entry(3f, 1600f),
            Entry(4f, 1400f),
            Entry(5f, 1100f),
            Entry(6f, 1600f),
        )

        renderChart(chart, actualEntries, predictedEntries)
    }

    fun renderChart(
        chart: LineChart,
        actual: List<Entry>,
        predicted: List<Entry>
    ) {
        val actualSet = LineDataSet(actual, "Doanh thu thực tế")
        actualSet.color = Color.BLUE
        actualSet.setCircleColor(Color.BLUE)
        actualSet.lineWidth = 3f

        val predictSet = LineDataSet(predicted, "Dự đoán AI")
        predictSet.color = Color.parseColor("#7F00FF")
        predictSet.setCircleColor(Color.parseColor("#7F00FF"))
        predictSet.lineWidth = 3f
        predictSet.enableDashedLine(10f, 5f, 0f)

//        Vẽ circle đặc
        actualSet.setDrawCircleHole(false)
        predictSet.setDrawCircleHole(false)

        val data = LineData(actualSet, predictSet)

        chart.data = data

        chart.animateX(800)

        chart.invalidate()
    }

    fun setupFilter(
        view: View,
        chart: LineChart
    ) {
        val weekBtn = view.findViewById<MaterialButton>(R.id.filterWeek)
        val monthBtn = view.findViewById<MaterialButton>(R.id.filterMonth)
        val yearBtn = view.findViewById<MaterialButton>(R.id.filterYear)
        val allBtn = view.findViewById<MaterialButton>(R.id.filterAll)

        val buttons = listOf(weekBtn, monthBtn, yearBtn, allBtn)

//        Gán Listener cho từng button
        buttons.forEach { button ->

            button.setOnClickListener {

//                Bỏ check tất cả button, sau đó check button vừa click
                buttons.forEach { it.isChecked = false }
                button.isChecked = true

                chart.clear()

//                Render chart theo button
                when (button.id) {
                    R.id.filterWeek -> loadWeekData(chart)
                    R.id.filterMonth -> loadMonthData(chart)
                    R.id.filterYear -> loadYearData(chart)
                    R.id.filterAll -> loadAllData(chart)
                }
            }
        }

//        Mặc định check button
        weekBtn.isChecked = true
    }
}
