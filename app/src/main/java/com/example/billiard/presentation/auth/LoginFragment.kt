package com.example.billiard.presentation.auth

import android.graphics.Color
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.billiard.BilliardApplication
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.network.Resource
import com.example.billiard.databinding.FragmentLoginBinding
import com.example.billiard.domain.usecase.auth.LoginUseCase
import kotlinx.coroutines.launch


class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val loginViewModel: LoginViewModel by viewModels {
        val appContainer = (requireActivity().application as BilliardApplication).container
        val loginusecase = LoginUseCase(appContainer.authRepository)
        LoginViewModel.Factory(loginusecase)
    }

    override fun setupViews() {
        binding.cardRoleAdmin.setOnClickListener {
            updateRoleUI(isAdminSelected = true)
        }
        binding.cardRoleStaff.setOnClickListener {
            updateRoleUI(isAdminSelected = false)
        }
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }
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
    private fun setupUI(){
        binding.cardRoleAdmin.setOnClickListener {
            updateRoleUI(isAdminSelected = true)
        }
        binding.cardRoleStaff.setOnClickListener {
            updateRoleUI(isAdminSelected = false)
        }
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
//            loginViewModel.login(email, password)
        }
    }
    private fun updateRoleUI(isAdminSelected: Boolean) {
        binding.cardRoleAdmin.isChecked = isAdminSelected
        binding.cardRoleStaff.isChecked = !isAdminSelected

        binding.cardRoleAdmin.strokeColor = if (isAdminSelected) Color.WHITE else Color.parseColor("#E0E0E0")
        binding.cardRoleStaff.strokeColor = if (!isAdminSelected) Color.WHITE else Color.parseColor("#E0E0E0")
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    loginViewModel.loginState.collect { state ->
                        when (state) {
                            is Resource.Success -> {
                                Toast.makeText(requireContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                            }

                            is Resource.Error -> {
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                            }

                            is Resource.Loading -> {
                                Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()
                            }

                            else -> {
                                null
                            }
                        }
                    }
                }

            }

        }
    }


}