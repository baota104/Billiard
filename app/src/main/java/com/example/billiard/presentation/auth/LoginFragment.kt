package com.example.billiard.presentation.auth

import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.billiard.core.utils.TokenManager
import com.example.billiard.databinding.FragmentLoginBinding
import com.example.billiard.di.AppContainer
import androidx.lifecycle.ViewModelProvider
import com.example.billiard.di.ViewModelFactory


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var tokenManager: TokenManager
    private lateinit var viewModel: LoginViewModel

    private var passwordVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(inflater, container, false)

        val containerDI = AppContainer(requireContext())
        tokenManager = containerDI.tokenManager
        
        val factory = ViewModelFactory(containerDI)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.btnTogglePassword.setOnClickListener {

            passwordVisible = !passwordVisible

            if (passwordVisible) {

                binding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

            } else {

                binding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
        }

        binding.btnLogin.setOnClickListener {

            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) return@setOnClickListener

            binding.loginLoading.visibility = View.VISIBLE

            viewModel.login(username, password) { response, errorMsg ->

                binding.loginLoading.visibility = View.GONE

                if (response != null) {

                    tokenManager.saveTokens(
                        response.access_token,
                        response.refresh_token
                    )

                    Toast.makeText(requireContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                    // navigate to home
                } else {
                    Toast.makeText(requireContext(), "Lỗi: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}