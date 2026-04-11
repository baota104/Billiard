package com.example.billiard.data.remote.api

import com.example.billiard.data.remote.dto.request.CreatePriceListRequest
import com.example.billiard.data.remote.dto.request.UpdatePriceListRequest
import com.example.billiard.data.remote.dto.response.ApiResponse
import com.example.billiard.data.remote.dto.response.PriceListDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PriceListApiService {

    @GET("api/v1/pricelists")
    suspend fun getPriceLists(): Response<ApiResponse<List<PriceListDto>>>

    @Headers("Content-Type: application/json")
    @POST("api/v1/pricelists")
    suspend fun createPriceList(
        @Body request: CreatePriceListRequest
    ): Response<ApiResponse<PriceListDto>>

    @Headers("Content-Type: application/json")
    @PUT("api/v1/pricelists")
    suspend fun updatePriceList(
        @Body request: UpdatePriceListRequest
    ): Response<ApiResponse<PriceListDto>>

    @DELETE("api/v1/pricelists/{priceListId}")
    suspend fun deletePriceList(
        @Path("priceListId") id: Int
    ): Response<ApiResponse<PriceListDto>>
}