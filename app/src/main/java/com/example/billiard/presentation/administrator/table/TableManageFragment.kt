package com.example.billiard.presentation.administrator.table

import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.databinding.FragmentTableManageBinding
import com.example.billiard.presentation.adapter.BanListAdapter
import com.example.billiard.presentation.model.BanUiModel


class TableManagementFragment : BaseFragment<FragmentTableManageBinding>(FragmentTableManageBinding::inflate) {

    private lateinit var tableAdapter: BanListAdapter

    override fun setupViews() {
        tableAdapter = BanListAdapter(
            onEditClick = { table ->
                Toast.makeText(requireContext(), "Sửa bàn: ${table.ten}", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { table ->
                Toast.makeText(requireContext(), "Xóa bàn: ${table.ten}", Toast.LENGTH_SHORT).show()
                // Mở dialog Xác nhận xóa ở đây
            }
        )

        binding.rvTableList.apply {
            adapter = tableAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.fabAddTable.setOnClickListener {
            Toast.makeText(requireContext(), "Thêm bàn mới", Toast.LENGTH_SHORT).show()
        }
    }

    override fun observeData() {
        val mockData = listOf(
            BanUiModel("B01", "Bàn 01 - VIP", "Đang chơi", "POOL", null),
            BanUiModel("B02", "Bàn 02 - Thường", "Trống", "SNOOKER", null),
            BanUiModel("B03", "Bàn 03 - Phăng", "Bảo trì", "CAROM", null),
            BanUiModel("B04", "Bàn 04 - VIP", "Đã đặt", "POOL", null),
            BanUiModel("B05", "Bàn 05 - Thường", "Đang chơi", "POOL", null)
        )
        tableAdapter.submitList(mockData)
    }
}