package com.example.billiard.presentation.home.homscreen

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.network.Resource
import com.example.billiard.core.utils.SessionManager
import com.example.billiard.databinding.FragmentHomeBinding
import com.example.billiard.domain.model.DashboardTable
import com.example.billiard.presentation.adapter.BanDashBoardAdapter
import com.example.billiard.presentation.home.bottomopentable.OpenTableBottomSheet
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: TableViewModel by viewModels()
    private lateinit var banAdapter: BanDashBoardAdapter

    // Tiêm SessionManager để lấy ID nhân viên đang đăng nhập phục vụ cho việc tạo hóa đơn
    @Inject
    lateinit var sessionManager: SessionManager

    private var allTables: List<DashboardTable> = emptyList()
    private var currentTabPosition = 0 

    override fun setupViews() {
        setUpRecycle()
        setUpClick()
    }

    private fun setUpRecycle() {
        banAdapter = BanDashBoardAdapter { clickedTable ->
            handleTableClick(clickedTable)
        }

        binding.rvTables.apply {
            adapter = banAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val visibleItemCount = layoutManager.childCount
                        val totalItemCount = layoutManager.itemCount
                        val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                        if (!viewModel.isLoadingMore && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            viewModel.loadTables() 
                        }
                    }
                }
            })
        }
    }

    private fun setUpClick() {
        binding.tabLayoutFilter.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTabPosition = tab?.position ?: 0
                applyFilter() 
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Kéo vuốt để tải lại trang
//        binding.swipeRefreshLayout.setOnRefreshListener {
//            viewModel.loadTables(isRefresh = true)
//            binding.swipeRefreshLayout.isRefreshing = false
//        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 1. Lắng nghe danh sách bàn
                launch {
                    viewModel.tablesState.collect { state ->
                        when (state) {
                            is Resource.Loading -> { }
                            is Resource.Success -> {
                                allTables = state.data
                                applyFilter()
                            }
                            is Resource.Error -> {
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                // 2. Lắng nghe kết quả khi bấm "Mở bàn"
                launch {
                    viewModel.openTableState.collect { state ->
                        when (state) {
                            is Resource.Loading -> { 
                                // Có thể hiện progress dialog
                            }
                            is Resource.Success -> {
                                Toast.makeText(requireContext(), "Mở bàn thành công!", Toast.LENGTH_SHORT).show()
                                viewModel.resetOpenTableState()
                            }
                            is Resource.Error -> {
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                                viewModel.resetOpenTableState()
                            }
                            null -> {}
                        }
                    }
                }
            }
        }
    }

    private fun applyFilter() {
        val filteredList = when (currentTabPosition) {
            0 -> allTables // Tất cả
            1 -> allTables.filter { it.status.equals("EMPTY", true) || it.status.equals("AVAILABLE", true) } // Trống
            2 -> allTables.filter { it.status.equals("PLAYING", true) || it.status.equals("RESERVED", true) } // Đang chơi
            else -> allTables.filter { it.status.equals("MAINTENANCE", true) || it.status.equals("MAINTAIN", true) } // Bảo trì
        }
        banAdapter.submitList(filteredList)
    }

    private fun handleTableClick(ban: DashboardTable) {
        val status = ban.status.uppercase()

        when (status) {
            "EMPTY", "AVAILABLE" -> {
                // Bàn trống -> Mở BottomSheet yêu cầu mở bàn
                val bottomSheet = OpenTableBottomSheet(ban = ban) { banDuocChon ->
                    // Lấy ID nhân viên từ SessionManager (Mặc định là 1 nếu chưa đăng nhập để test)
                    val employeeId = sessionManager.getEmployeeId() ?: 1 
                    viewModel.openTable(employeeId = employeeId, tableId = banDuocChon.id)
                }
                bottomSheet.show(childFragmentManager, "OpenTableBottomSheet")
            }
            
            "PLAYING", "RESERVED" -> {
                // Bàn đang chơi -> Chuyển sang màn hình xem chi tiết (Order/Menu)
                val bundle = Bundle().apply {
                    putInt("INVOICE_ID", ban.activeInvoice!!.id)
                }
                findNavController().navigate(R.id.action_homeFragment_to_banDetailFragment,bundle)
            }
            
            "MAINTENANCE", "MAINTAIN" -> {
                // Đang bảo trì -> Hiện thông báo cảnh báo
                Toast.makeText(requireContext(), "Bàn ${ban.name} đang được bảo trì, không thể mở!", Toast.LENGTH_SHORT).show()
            }
            
            else -> {
                Toast.makeText(requireContext(), "Trạng thái bàn không xác định!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}