package com.example.billiard.core.base

import android.util.Log
import com.example.billiard.core.network.Resource
import com.example.billiard.data.remote.dto.response.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

abstract class BaseRepository {

    protected val TAG = this::class.java.simpleName

    /**
     * @param apiCall Hàm gọi API bằng Retrofit
     * @param mapper Hàm chuyển đổi DTO (T) sang Domain Model (R)
     * @param onSuccess Hàm callback tùy chọn chạy sau khi API thành công (dùng để lưu SharePrefs, Database,...)
     */
    protected suspend fun <T, R> safeApiCall(
        apiCall: suspend () -> Response<ApiResponse<T>>,
        mapper: (T) -> R,
        onSuccess: suspend (T) -> Unit = {}
    ): Resource<R> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    // Giả định backend trả về status = 200 là thành công
                    if (apiResponse != null && (apiResponse.status == 200 || apiResponse.status == 201) && apiResponse.body != null) {
                        onSuccess(apiResponse.body) // Lưu dữ liệu nếu cần
                        return@withContext Resource.Success(mapper(apiResponse.body))
                    } else {
                        val message = apiResponse?.message ?: "Lỗi xử lý từ Server (Mã: ${apiResponse?.status})"
                        return@withContext Resource.Error(message)
                    }
                }

                // Xử lý các mã lỗi HTTP (4xx, 5xx)
                val errorMessage = when (response.code()) {
                    401 -> "lỗi 401, không tìm thấy"
                    403 -> "Bạn không có quyền thực hiện hành động này."
                    404 -> "Không tìm thấy dữ liệu (404)."
                    in 500..599 -> "Hệ thống Server đang lỗi. Vui lòng quay lại sau."
                    else -> "Lỗi kết nối: ${response.message()}"
                }
                
                // Đọc thêm chi tiết lỗi từ ErrorBody nếu có
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "API Error HTTP ${response.code()}: $errorBody")
                
                Resource.Error(errorMessage)

            } catch (e: Exception) {
                Log.e(TAG, "API Exception: ${e.message}", e)
                // Xử lý các lỗi gián đoạn do thiết bị/mạng
                val errorMessage = when (e) {
                    is SocketTimeoutException -> "Hết thời gian kết nối (Timeout). Vui lòng thử lại."
                    is IOException -> "Không có kết nối mạng. Vui lòng kiểm tra Wifi/3G."
                    is HttpException -> "Lỗi giao thức mạng: ${e.code()}"
                    else -> "Đã xảy ra lỗi không xác định."
                }
                Resource.Error(errorMessage, e)
            }
        }
    }

    /**
     * Fallback không cần mapper (T trả về thẳng là T)
     */
    protected suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<ApiResponse<T>>,
        onSuccess: suspend (T) -> Unit = {}
    ): Resource<T> {
        return safeApiCall(apiCall, { it }, onSuccess)
    }
}