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
        with(binding) {

            // 1. Quản lý Bàn
            cardQuanLyBan.setOnDetailsClickListener {
                findNavController().navigate(R.id.action_administraitorFragment_to_tableManageFragment)
            }

            // 2. Quản lý Khung giờ
            cardQuanLyKhungGio.setOnDetailsClickListener {
                findNavController().navigate(R.id.action_administraitorFragment_to_timeSlotManagementFragmentFragment)
            }

            // 3. Quản lý Nhân viên
            cardQuanLyNhanVien.setOnDetailsClickListener {
                findNavController().navigate(R.id.action_administraitorFragment_to_employeeManagementFragment)
            }

            // 4. Quản lý Voucher
            cardQuanLyVC.setOnDetailsClickListener {
                findNavController().navigate(R.id.action_administraitorFragment_to_voucherFragment)
            }

            // 5. Quản lý Kho hàng
            cardQuanLyKhohang.setOnDetailsClickListener {
                findNavController().navigate(R.id.action_administraitorFragment_to_inventoryManagementFragment)
            }
            cardQuanLyBank.setOnDetailsClickListener {
                findNavController().navigate(R.id.action_administraitorFragment_to_bankFragment)
            }
        }
    }


    override fun observeData() {
    }
}