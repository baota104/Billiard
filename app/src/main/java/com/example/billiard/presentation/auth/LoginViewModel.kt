package com.example.billiard.presentation.auth

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billiard.data.remote.dto.LoginResponse
import com.example.billiard.domain.usecase.auth.LoginUseCase
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    fun login(
        username: String,
        password: String,
        onResult: (LoginResponse?, String?) -> Unit
    ) {

        viewModelScope.launch {

            try {

                val response = loginUseCase.execute(username, password)

                onResult(response, null)

            } catch (e: HttpException) {
                
                val errorBody = e.response()?.errorBody()?.string()
                var errorMsg = errorBody
                
                // Parse JSON để lấy đúng dòng "error_description" cho chữ ngắn lại, dễ đọc trên điện thoại
                try {
                    if (errorBody != null) {
                        val jsonObject = JSONObject(errorBody)
                        errorMsg = jsonObject.optString("error_description", errorBody)
                    }
                } catch (jsonException: Exception) {
                    // Nếu không phải chuẩn JSON thì giữ nguyên
                }
                
                onResult(null, "Từ chối: $errorMsg")
                
            } catch (e: Exception) {

                onResult(null, "Lỗi mạng: ${e.message}")
            }
        }
    }
}