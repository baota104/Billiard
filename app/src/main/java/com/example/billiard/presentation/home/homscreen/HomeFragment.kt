package com.example.billiard.presentation.home.homscreen

import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.databinding.FragmentHomeBinding
import com.example.billiard.presentation.adapter.BanDashBoardAdapter
import com.example.billiard.presentation.home.bottomopentable.OpenTableBottomSheet
import com.example.billiard.domain.model.BanUiModel
import com.example.billiard.domain.model.TableStatus
import com.google.android.material.tabs.TabLayout

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {


    private var allTables = listOf<BanUiModel>()
    private lateinit var banAdapter: BanDashBoardAdapter

    override fun setupViews() {
        createMockData()
        setUpClick()
        setUpRecycle()
        filterDataByTab(0)


    }
    private fun setUpClick(){
        binding.tabLayoutFilter.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                filterDataByTab(tab?.position ?: 0)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
    private fun setUpRecycle(){
        banAdapter = BanDashBoardAdapter { onclick ->
            handleTableClick(onclick)
        }
        binding.rvTables.apply {
            adapter = banAdapter
        }


    }
    private fun handleTableClick(ban: BanUiModel) {
        if (ban.status == TableStatus.EMPTY) {
            val bottomSheet = OpenTableBottomSheet(ban = ban) { banDuocChon ->
                Toast.makeText(
                    requireContext(),
                    "Đang mở bàn ${banDuocChon.name}...",
                    Toast.LENGTH_SHORT
                ).show()

            }
            bottomSheet.show(childFragmentManager, "OpenTableBottomSheet")
        } else if(ban.status == TableStatus.PLAYING){
            findNavController().navigate(R.id.action_homeFragment_to_banDetailFragment)
            Toast.makeText(requireContext(), "Bàn đang chơi", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(requireContext(), "Bàn đang bảo trì", Toast.LENGTH_SHORT).show()
        }
    }

    override fun observeData() {
    }

    private fun createMockData() {
        allTables = listOf(
            BanUiModel("1", "Bàn 01","80.000", "Bida Lỗ", TableStatus.PLAYING, "01:25:00"),
            BanUiModel("2", "Bàn 02", "80.000","Snooker", TableStatus.EMPTY),
            BanUiModel("3", "Bàn 03","80.000", "Bida Lỗ", TableStatus.PLAYING, "00:42:15"),
            BanUiModel("4", "Bàn 04", "80.000","VIP", TableStatus.EMPTY),
            BanUiModel("5", "Bàn 05", "80.000","Bida Lỗ", TableStatus.EMPTY),
            BanUiModel("6", "Bàn 06", "80.000","Snooker", TableStatus.MAINTAIN)
        )
    }

    private fun filterDataByTab(position: Int) {
        val filteredList = when (position) {
            0 -> allTables // Tab "Tất cả"
            1 -> allTables.filter { it.status == TableStatus.EMPTY } // Tab "Trống"
            2 -> allTables.filter { it.status == TableStatus.PLAYING } // Tab "Đang chơi"
            3 -> allTables.filter { it.status == TableStatus.MAINTAIN } // Tab "Bảo trì"
            else -> allTables
        }
         banAdapter.submitList(filteredList)
        Toast.makeText(requireContext(), "Lọc: ${filteredList.size} bàn", Toast.LENGTH_SHORT).show()
    }
}