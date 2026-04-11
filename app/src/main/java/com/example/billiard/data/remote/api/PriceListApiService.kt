package com.example.billiard.data.remote.api

import com.example.billiard.data.remote.dto.request.CreatePriceListRequest
import com.example.billiard.data.remote.dto.request.UpdatePriceListRequest
import com.example.billiard.data.remote.dto.response.ApiResponse
import com.example.billiard.data.remote.dto.response.PriceListDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PriceListApiService {

    @GET("api/v1/pricelists")
    suspend fun getPriceLists(): Response<ApiResponse<List<PriceListDto>>>

    @POST("api/v1/pricelists")
    suspend fun createPriceList(
        @Body request: CreatePriceListRequest
    ): Response<ApiResponse<PriceListDto>>

    @PUT("api/v1/pricelists")
    suspend fun updatePriceList(
        @Body request: UpdatePriceListRequest
    ): Response<ApiResponse<PriceListDto>>

    @DELETE("api/v1/pricelists/{id}")
    suspend fun deletePriceList(
        @Path("id") id: Int
    ): Response<ApiResponse<PriceListDto>> // Backend trả về obj PriceList đã xóa, nhưng app thường chỉ cần biết thành công
}