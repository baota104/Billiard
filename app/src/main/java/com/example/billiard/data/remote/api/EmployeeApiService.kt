package com.example.billiard.data.remote.api

import com.example.billiard.data.remote.dto.request.CreateEmployeeRequest
import com.example.billiard.data.remote.dto.request.UpdateEmployeeRequest
import com.example.billiard.data.remote.dto.response.ApiResponse
import com.example.billiard.data.remote.dto.response.EmployeeDto
import com.example.billiard.data.remote.dto.response.PageDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface EmployeeApiService {
    @GET("api/v1/employees")
    suspend fun getEmployees(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PageDto<EmployeeDto>>>

    @Headers("Content-Type: application/json")
    @POST("api/v1/employees")
    suspend fun createEmployee(
        @Body request: CreateEmployeeRequest
    ): Response<ApiResponse<Any>> // Hứng body tuỳ ý hoặc rỗng, quan tâm tới status 200

    @Headers("Content-Type: application/json")
    @PUT("api/v1/employees/update")
    suspend fun updateEmployee(
        @Body request: UpdateEmployeeRequest
    ): Response<ApiResponse<EmployeeDto>>

    @DELETE("api/v1/employees/{employeeId}")
    suspend fun deleteEmployee(
        @Path("employeeId") employeeId: Int
    ): Response<ApiResponse<String>>
}