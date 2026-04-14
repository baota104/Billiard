package com.example.billiard.data.remote.api


import com.example.billiard.data.remote.dto.request.CreateOrderDetailRequest
import com.example.billiard.data.remote.dto.response.ApiResponse
import com.example.billiard.data.remote.dto.response.OrderDetailDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderDetailApiService {

    // 1. API GỌI MÓN (THÊM ORDER DETAIL)
    @POST("api/v1/order-details")
    suspend fun createOrderDetail(
        @Body request: CreateOrderDetailRequest
    ): Response<OrderDetailDto>

    // 2. API HỦY MÓN (XÓA ORDER DETAIL)
    @DELETE("api/v1/order-details/{id}")
    suspend fun deleteOrderDetail(
        @Path("id") id: Int
    ): Response<ApiResponse<Unit>> // Dùng Unit nếu Server chỉ trả về status 200 mà không kèm body phức tạp
}