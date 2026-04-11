package com.example.billiard.data.remote.api

import com.example.billiard.data.remote.dto.request.CreateInvoiceRequest
import com.example.billiard.data.remote.dto.request.UpdateInvoiceRequest
import com.example.billiard.data.remote.dto.response.ApiResponse
import com.example.billiard.data.remote.dto.response.InvoiceDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface InvoiceApiService {

    @GET("api/v1/invoices")
    suspend fun getInvoices(): Response<ApiResponse<List<InvoiceDto>>>

    @GET("api/v1/invoices/{invoiceId}")
    suspend fun getInvoiceById(
        @Path("invoiceId") invoiceId: Int
    ): Response<ApiResponse<InvoiceDto>>

    @POST("api/v1/invoices")
    suspend fun createInvoice(
        @Body request: CreateInvoiceRequest
    ): Response<ApiResponse<InvoiceDto>>

    @PUT("api/v1/invoices")
    suspend fun updateInvoice(
        @Body request: UpdateInvoiceRequest
    ): Response<ApiResponse<InvoiceDto>>

    @DELETE("api/v1/invoices/{invoiceId}")
    suspend fun deleteInvoice(
        @Path("invoiceId") invoiceId: Int
    ): Response<ApiResponse<String>>
}