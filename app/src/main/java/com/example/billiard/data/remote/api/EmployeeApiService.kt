package com.example.billiard.data.remote.api

import com.example.billiard.data.remote.dto.request.CreateEmployeeRequest
import com.example.billiard.data.remote.dto.response.ApiResponse
import com.example.billiard.data.remote.dto.response.EmployeeDto
import com.example.billiard.data.remote.dto.response.PageDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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

    // Trong Swagger, nếu các trường như firstName, lastName... được tách rời ra từng ô text input
    // thay vì nằm trong 1 cục JSON tổng duy nhất, thì đó là Multipart Form-Data (@ModelAttribute).
    // Do đó Android phải dùng @Multipart và @Part thay vì @Body
    @Multipart
    @PUT("api/v1/employees/update")
    suspend fun updateEmployee(
        @Part("id") id: RequestBody,
        @Part("firstName") firstName: RequestBody,
        @Part("lastName") lastName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phoneNumber") phoneNumber: RequestBody,
        @Part("role") role: RequestBody,
        @Part("isActive") isActive: RequestBody
    ): Response<ApiResponse<EmployeeDto>>

    @DELETE("api/v1/employees/{employeeId}")
    suspend fun deleteEmployee(
        @Path("employeeId") employeeId: Int
    ): Response<ApiResponse<String>>
}