package com.example.billiard.presentation.administrator.admin

import androidx.navigation.fragment.findNavController
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.databinding.FragmentAdministraitorBinding
import com.example.billiard.presentation.adapter.BanListAdapter

class AdministraitorFragment : BaseFragment<FragmentAdministraitorBinding>(
    FragmentAdministraitorBinding::inflate) {
    private lateinit var tableAdapter: BanListAdapter
    override fun setupViews() {
        binding.btnChiTietBan.setOnClickListener {
            findNavController().navigate(R.id.action_administraitorFragment_to_tableManageFragment)
        }

        binding.btnChiTietKhungGio.setOnClickListener {
            findNavController().navigate(R.id.action_administraitorFragment_to_timeSlotManagementFragmentFragment)
        }

        binding.btnChiTietNhanVien.setOnClickListener {
            findNavController().navigate(R.id.action_administraitorFragment_to_employeeManagementFragment)
        }
        binding.btnChiTietVC.setOnClickListener {
            findNavController().navigate(R.id.action_administraitorFragment_to_voucherFragment)
        }
        binding.btnChiTietKhoHang.setOnClickListener {
            findNavController().navigate(R.id.action_administraitorFragment_to_inventoryManagementFragment)
        }


    }


    override fun observeData() {
    }
}