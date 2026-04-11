package com.example.billiard.data.remote.api

import com.example.billiard.data.remote.dto.request.CreateVoucherRequest
import com.example.billiard.data.remote.dto.request.UpdateVoucherRequest
import com.example.billiard.data.remote.dto.response.ApiResponse
import com.example.billiard.data.remote.dto.response.VoucherDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface VoucherApiService {

    @GET("api/v1/vouchers")
    suspend fun getActiveVouchers(): Response<ApiResponse<List<VoucherDto>>>

    @GET("api/v1/vouchers/{voucherId}")
    suspend fun getVoucherById(
        @Path("voucherId") voucherId: Long
    ): Response<ApiResponse<VoucherDto>>

    @POST("api/v1/vouchers")
    suspend fun createVoucher(
        @Body request: CreateVoucherRequest
    ): Response<ApiResponse<VoucherDto>>

    @PATCH("api/v1/vouchers/{voucherId}")
    suspend fun updateVoucher(
        @Path("voucherId") voucherId: Long,
        @Body request: UpdateVoucherRequest
    ): Response<ApiResponse<VoucherDto>>

    // LƯU Ý: Tuy tài liệu BE ghi là GET nhưng lại đính kèm @RequestBody. 
    // Các HTTP Client chuẩn (như Retrofit/OkHttp) sẽ ném lỗi nếu gọi GET mà nhét Body.
    // Thường trong Spring Boot, endpoint như vậy sẽ là POST. Tạm thời mình setup là POST để an toàn.
    @POST("api/v1/vouchers/validate_vouchers")
    suspend fun validateVouchers(
        @Body totalBillAmount: Double
    ): Response<ApiResponse<List<VoucherDto>>>

    @DELETE("api/v1/vouchers/{voucherId}")
    suspend fun deleteVoucher(
        @Path("voucherId") voucherId: Long
    ): Response<ApiResponse<String>>
}