package com.example.billiard.presentation.stastics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.billiard.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StasticsFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stastics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo Chart Manager
        viewModel.initializeChart(requireContext())

        // Setup Chart và tải dữ liệu mặc định
        viewModel.setupChart(view)
    }
}