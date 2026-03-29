package com.example.billiard.core.network

import com.example.billiard.core.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        if (originalRequest.url.encodedPath.contains("/api/v1/auth/login")) {
            return chain.proceed(originalRequest)
        }
        val requestBuilder = originalRequest.newBuilder()


        val token = sessionManager.getAccessToken()

        // Nếu có Token, tự động đính kèm vào Header với tiền tố "Bearer "
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        // 3. Cho phép Request tiếp tục bay lên Server
        return chain.proceed(requestBuilder.build())
    }
}
