package com.example.billiard.presentation.stastics

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.billiard.R
import com.example.billiard.core.network.Resource
import com.example.billiard.core.utils.LoadingUtils
import com.example.billiard.domain.model.DashboardSummary
import com.example.billiard.presentation.stastics.charts.RevenueChartManager
import com.github.mikephil.charting.charts.LineChart
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class StasticsFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var chartManager: RevenueChartManager
    private lateinit var lineChart: LineChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stastics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lineChart = view.findViewById(R.id.revenueChart)
        chartManager = RevenueChartManager(requireContext())
        chartManager.setupChart(lineChart)

        setupFilterButtons(view)

        observeChartData()
        observeSummaryData()
    }

    private fun setupFilterButtons(view: View) {
        val weekBtn = view.findViewById<MaterialButton>(R.id.filterWeek)
        val monthBtn = view.findViewById<MaterialButton>(R.id.filterMonth)
        val yearBtn = view.findViewById<MaterialButton>(R.id.filterYear)
        val allBtn = view.findViewById<MaterialButton>(R.id.filterAll)
        val button2 = view.findViewById<FrameLayout>(R.id.billDetailNavigationCard)
        val buton3 = view.findViewById<FrameLayout>(R.id.topOrderNavigationCard)
        buton3.setOnClickListener {
            findNavController().navigate(R.id.action_stasticsFragment_to_itemStatisticsFragment)
        }

            button2.setOnClickListener {
            // Lưu ý nhỏ: Kiểm tra xem Action ID này có đúng là đi từ StasticsFragment không nhé
            findNavController().navigate(R.id.action_stasticsFragment_to_invoiceFragment)
        }
        val buttons = listOf(weekBtn, monthBtn, yearBtn, allBtn)

        val filterMap = mapOf(
            weekBtn to "WEEKLY",
            monthBtn to "MONTHLY",
            yearBtn to "YEARLY",
            allBtn to "ALL"
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                buttons.forEach { it.isChecked = false }
                button.isChecked = true

                val apiFilter = filterMap[button] ?: "WEEKLY"
                viewModel.loadDashboardRevenue(apiFilter)
            }
        }
    }

    private fun observeChartData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.revenueState.collect { state ->
                    when (state) {
                        is Resource.Loading -> {
                            LoadingUtils.show(requireContext())
                            lineChart.clear()
                        }
                        is Resource.Success -> {
                            LoadingUtils.hide()
                            val revenueData = state.data
                            chartManager.updateChartData(lineChart, revenueData)
                        }
                        is Resource.Error -> {
                            LoadingUtils.hide()
                            Log.e("ChartError", state.message)
                            Toast.makeText(requireContext(), "Lỗi tải biểu đồ", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    // ==========================================================
    // XỬ LÝ DỮ LIỆU TỔNG QUAN (DASHBOARD SUMMARY)
    // ==========================================================
    private fun observeSummaryData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.summaryState.collect { state ->
                    when (state) {
                        is Resource.Loading -> {
                            LoadingUtils.show(requireContext())
                        }
                        is Resource.Success -> {
                            LoadingUtils.hide()
                            val summaryData = state.data
                            updateSummaryUI(requireView(), summaryData)
                        }
                        is Resource.Error -> {
                            LoadingUtils.hide()
                            Log.e("StasticsFragment", "Lỗi tải tổng quan: ${state.message}")
                        }
                    }
                }
            }
        }
    }

    private fun updateSummaryUI(view: View, summary: DashboardSummary) {
        // 1. Ô DOANH THU HÔM NAY
        val revenue = summary.revenueSummary
        view.findViewById<TextView>(R.id.revenueDetailText).text = formatCurrency(revenue.todayRevenue)
        val tvRevChange = view.findViewById<TextView>(R.id.revenueSubDetailText)
        tvRevChange.text = formatPercentageString(revenue.changePercentage)
        applyTrendColor(tvRevChange, revenue.changePercentage)

        // 2. Ô THỜI GIAN CHƠI TRUNG BÌNH
        val playtime = summary.playtimeSummary
        view.findViewById<TextView>(R.id.playtimeDetailText).text = "${String.format("%.1f", playtime.avgPlaytime)}h"
        val tvPlayChange = view.findViewById<TextView>(R.id.playtimeSubDetailText)
        tvPlayChange.text = formatPercentageString(playtime.changePercentage)
        applyTrendColor(tvPlayChange, playtime.changePercentage)

        // 3. Ô TỈ LỆ TĂNG TRƯỞNG
        val growth = summary.growthSummary
        view.findViewById<TextView>(R.id.growthDetailText).text = "${String.format("%.1f", growth.growthRate)}%"
        val tvGrowthChange = view.findViewById<TextView>(R.id.growthSubDetailText)
        tvGrowthChange.text = formatPercentageString(growth.growthRate) // Giả sử growthRate là chỉ số tổng quát
        applyTrendColor(tvGrowthChange, growth.growthRate)

        // 4. Ô CÔNG SUẤT BÀN
        val table = summary.tableSummary
        view.findViewById<TextView>(R.id.tableRateDetailText).text = "${table.activeTables}/${table.totalTables}"
        view.findViewById<TextView>(R.id.tableRateSubDetailText).text = "${String.format("%.1f", table.utilizationRate)}% công suất"
    }


    // ==========================================================
    // CÁC HÀM TIỆN ÍCH (UTILS)
    // ==========================================================

    private fun formatCurrency(amount: Double): String {
        return "%,.0f đ".format(amount).replace(',', '.')
    }

    // Tự động thêm dấu + hoặc - và chữ "so với kỳ trước"
    private fun formatPercentageString(value: Double): String {
        val sign = if (value > 0) "+" else if (value < 0) "-" else ""
        return "$sign${String.format("%.1f", abs(value))}% so với kỳ trước"
    }

    // Đổi màu text: Tăng -> Xanh lá (#10B981), Giảm -> Đỏ (#EF4444), Đứng im -> Xám
    private fun applyTrendColor(textView: TextView, value: Double) {
        val colorHex = when {
            value > 0 -> "#10B981" // Xanh lá mượt
            value < 0 -> "#EF4444" // Đỏ cảnh báo
            else -> "#6B7280"      // Xám trung tính
        }
        textView.setTextColor(Color.parseColor(colorHex))
    }
}