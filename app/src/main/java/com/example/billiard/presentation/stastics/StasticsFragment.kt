package com.example.billiard.presentation.stastics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.billiard.R
import com.example.billiard.presentation.stastics.charts.RevenueChartManager
import com.github.mikephil.charting.charts.LineChart

// lateinit: Khai báo biến nhưng chưa khởi tạo
private lateinit var chartManager: RevenueChartManager
class StasticsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stastics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chart = view.findViewById<LineChart>(R.id.revenueChart)

//        Khởi tạo và setup chart
        chartManager = RevenueChartManager(requireContext())
        chartManager.setupChart(chart)
        chartManager.loadWeekData(chart)
        chartManager.setupFilter(view, chart)
    }
}