package com.example.billiard.presentation.stastics

import android.R.attr.data
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.databinding.FragmentItemStatisticsBinding
import com.example.billiard.presentation.adapter.TopProductAdapter
import com.example.billiard.presentation.adapter.TopProductItem
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemStatisticsFragment : BaseFragment<FragmentItemStatisticsBinding>(FragmentItemStatisticsBinding::inflate) {

    private lateinit var topProductAdapter: TopProductAdapter

    override fun setupViews() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        setupRecyclerView()
        setupPieChart()

        // Đổ dữ liệu giả (Mock Data) lên màn hình
        loadMockData()
    }

    private fun setupRecyclerView() {
        topProductAdapter = TopProductAdapter()
        binding.rvTopProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = topProductAdapter
        }
    }

    private fun setupPieChart() {
        val pieChart = binding.pieChart

        // Cấu hình UI cho PieChart
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = false // Biểu đồ dạng tròn kín, không có lỗ ở giữa
        pieChart.setExtraOffsets(20f, 0f, 20f, 0f)

        // Tắt legend (chú thích ô vuông) vì ta dùng text chỉa ra ngoài
        pieChart.legend.isEnabled = false

        // Animation
        pieChart.animateY(1400)
    }

    private fun loadMockData() {
        // 1. Tạo danh sách dữ liệu giả lập (có logic khớp nhau)
        // Số lượng giảm dần để xếp hạng từ 1 đến 5
        val mockList = listOf(
            TopProductItem("Coca Cola", 125, 1875000.0),  // Bán chạy nhất
            TopProductItem("Cà phê đá", 98, 1470000.0),
            TopProductItem("Sting", 85, 1275000.0),
            TopProductItem("Cà phê sữa", 70, 1400000.0),
            TopProductItem("Nước suối", 55, 550000.0)     // Bán ít nhất
        )

        // Đẩy vào RecyclerView
        topProductAdapter.submitList(mockList)

        // 2. Tự động map dữ liệu từ list trên vào PieChart
        val entries = ArrayList<PieEntry>()
        for (item in mockList) {
            // Đưa thẳng số lượng (sold) vào, thư viện sẽ tự động tính ra % chính xác!
            entries.add(PieEntry(item.sold.toFloat(), item.name))
        }

        // Bảng màu giống thiết kế
        val colors = arrayListOf(
            Color.parseColor("#3B82F6"), // Xanh dương (Coca Cola)
            Color.parseColor("#22C55E"), // Xanh lá (Cà phê đá)
            Color.parseColor("#A855F7"), // Tím (Sting)
            Color.parseColor("#EAB308"), // Vàng (Cà phê sữa)
            Color.parseColor("#F97316")  // Cam (Nước suối)
        )

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors



        // --- CẤU HÌNH LABEL CHỈA RA NGOÀI ---
        dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        dataSet.valueLinePart1OffsetPercentage = 80f // Độ dài đoạn vạch
        dataSet.valueLinePart1Length = 0.2f
        dataSet.valueLinePart2Length = 0.4f
        dataSet.valueLineColor = Color.TRANSPARENT // Ẩn đường kẻ nối
        binding.pieChart.setEntryLabelColor(Color.BLACK)

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(binding.pieChart))
        data.setValueTextSize(11f)

        binding.pieChart.data = data
        binding.pieChart.invalidate()
    }

    override fun observeData() {
        // Hứng API ở đây sau này
    }
}