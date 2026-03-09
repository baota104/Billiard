package com.example.billiard.presentation.administrator.admin

import androidx.navigation.fragment.findNavController
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.databinding.FragmentAdministraitorBinding

class AdministraitorFragment : BaseFragment<FragmentAdministraitorBinding>(
    FragmentAdministraitorBinding::inflate) {

    override fun setupViews() {
        binding.btnChiTietBan.setOnClickListener {
            findNavController().navigate(R.id.action_adminFragment_to_tableManagementFragment)
        }

        binding.btnChiTietKhungGio.setOnClickListener {
            findNavController().navigate(R.id.action_adminFragment_to_timeManagementFragment)
        }
    }

    override fun observeData() {
    }
}