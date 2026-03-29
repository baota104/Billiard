package com.example.billiard.presentation.auth

import android.graphics.Color
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.billiard.BilliardApplication
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.ext.showToast
import com.example.billiard.core.network.Resource
import com.example.billiard.databinding.FragmentLoginBinding
import com.example.billiard.domain.model.UserAuth
import com.example.billiard.domain.usecase.auth.LoginUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val viewModel: LoginViewModel by viewModels()
    override fun setupViews() {
        setUpClick()
    }
    private fun setUpClick() {
        // 1. Chọn Role
        binding.cardRoleAdmin.setOnClickListener {
            updateRoleUI(isAdminSelected = true)
        }
        binding.cardRoleStaff.setOnClickListener {
            updateRoleUI(isAdminSelected = false)
        }

        // 2. Logic Nút Đăng nhập (CHỈ GỌI VIEWMODEL)
        binding.btnLogin.setOnClickListener {
            val user = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()

            if (user.isEmpty() || pass.isEmpty()) {
                showToast("Vui lòng nhập đầy đủ thông tin")
                return@setOnClickListener
            }

            // Gọi ViewModel xử lý (Nó sẽ kích hoạt vòng quay Loading và gọi API)
            viewModel.login(user, pass)
        }

        // 3. Ẩn/Hiện Mật khẩu
        var isPasswordVisible = false
        binding.btnTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.btnTogglePassword.setImageResource(R.drawable.ic_eye_on)
            } else {
                binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.btnTogglePassword.setImageResource(R.drawable.ic_eye_off)
            }
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }
    }
    private fun updateRoleUI(isAdminSelected: Boolean) {
        binding.cardRoleAdmin.isChecked = isAdminSelected
        binding.cardRoleStaff.isChecked = !isAdminSelected

        binding.cardRoleAdmin.strokeColor = if (isAdminSelected) Color.WHITE else Color.parseColor("#E0E0E0")
        binding.cardRoleStaff.strokeColor = if (!isAdminSelected) Color.WHITE else Color.parseColor("#E0E0E0")
    }

    override fun observeData() {
        // Thu thập dữ liệu từ StateFlow một cách an toàn theo Vòng đời Fragment
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collect { state ->
                    when (state) {
                        is Resource.Loading -> {
                            setLoadingState(true)
                        }
                        is Resource.Success -> {
                            setLoadingState(false)
                            handleLoginSuccess(state.data)
                            // Reset state để khi ấn Back quay lại màn hình này không bị dính logic cũ
                            viewModel.resetState()
                        }
                        is Resource.Error -> {
                            setLoadingState(false)
                            showToast(state.message)
                            viewModel.resetState()
                        }
                        null -> { /* Trạng thái khởi tạo ban đầu, không làm gì cả */ }
                    }
                }
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
//            binding.progressBar.show()
            binding.btnLogin.isEnabled = false
            binding.btnLogin.text = ""
        } else {
//            binding.progressBar.hide()
            binding.btnLogin.isEnabled = true
            binding.btnLogin.text = "Đăng nhập"
        }
    }

    private fun handleLoginSuccess(userAuth: UserAuth) {
        showToast("Xin chào ${userAuth.fullName}!")
        Log.d("LoginFragment", "Role: ${userAuth.role} ")
        val action = NavOptions.Builder()
            .setPopUpTo(R.id.loginFragment,true)
            .build()
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment,null
            ,action
        )
    }
}