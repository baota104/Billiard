package com.example.billiard.data.remote.api

import com.example.billiard.data.remote.dto.request.CreatePurchaseInvoiceRequest
import com.example.billiard.data.remote.dto.response.ApiResponse
import com.example.billiard.data.remote.dto.response.PurchaseHistoryDto
import com.example.billiard.data.remote.dto.response.PurchaseInvoiceDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PurchaseApiService {

    @Headers("Content-Type: application/json")
    @POST("api/v1/purchase-invoices")
    suspend fun createPurchaseInvoice(
        @Body request: CreatePurchaseInvoiceRequest
    ): Response<ApiResponse<PurchaseInvoiceDto>>

    @GET("api/v1/purchase-invoices/{invoiceId}")
    suspend fun getPurchaseInvoiceById(
        @Path("invoiceId") invoiceId: Int
    ): Response<ApiResponse<PurchaseInvoiceDto>>

    @DELETE("api/v1/purchase-invoices")
    suspend fun deletePurchaseInvoice(
        @Query("invoiceId") invoiceId: Int
    ): Response<ApiResponse<String>>

    @GET("api/v1/purchase-invoices/search")
    suspend fun searchPurchaseInvoices(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Response<ApiResponse<List<PurchaseHistoryDto>>>
}