package com.example.billiard.presentation.administrator.admin

import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.utils.SessionManager
import com.example.billiard.databinding.FragmentAdministraitorBinding
import com.example.billiard.presentation.adapter.BanListAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class AdministraitorFragment : BaseFragment<FragmentAdministraitorBinding>(
    FragmentAdministraitorBinding::inflate) {
    @Inject
    lateinit var sessionManager: SessionManager

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
            binding.btnLogout.setOnClickListener {
                // 1. Xóa dữ liệu phiên đăng nhập
                sessionManager.clearSession()

                // 2. Chuyển hướng về Login và dọn sạch TOÀN BỘ BackStack
                val navOptions = NavOptions.Builder()
                    // Lấy ID của toàn bộ sơ đồ điều hướng hiện tại và xóa sạch sành sanh
                    .setPopUpTo(findNavController().graph.id, inclusive = true)
                    .build()

                findNavController().navigate(R.id.loginFragment, null, navOptions)
            }
        }
    }


    override fun observeData() {
    }
}