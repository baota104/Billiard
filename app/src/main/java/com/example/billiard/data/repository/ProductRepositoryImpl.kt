package com.example.billiard.data.repository

import com.example.billiard.core.base.BaseRepository
import com.example.billiard.core.network.Resource
import com.example.billiard.data.mapper.toDomain
import com.example.billiard.data.mapper.toDto
import com.example.billiard.data.remote.api.ProductApiService
import com.example.billiard.domain.model.Product
import com.example.billiard.domain.request.UpsertProductParam
import com.example.billiard.domain.repository.ProductRepository
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApiService
) : BaseRepository(), ProductRepository {

    override suspend fun getAllProducts(): Resource<List<Product>> {
        return safeApiCall(
            apiCall = { api.getAllProducts() },
            mapper = { listDto -> listDto.map { it.toDomain() } }
        )
    }

    override suspend fun searchProducts(keyword: String): Resource<List<Product>> {
        return safeApiCall(
            apiCall = { api.searchProducts(keyword) },
            mapper = { listDto -> listDto.map { it.toDomain() } }
        )
    }

    override suspend fun getProductById(productId: Int): Resource<Product> {
        return safeApiCall(
            apiCall = { api.getProductById(productId) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun upsertProduct(param: UpsertProductParam): Resource<Product> {
        // 1. Chuyển DTO thành JSON String
        val productDto = param.toDto()
        val productJson = Gson().toJson(productDto)
        
        // 2. Tạo RequestBody cho JSON (Spring Boot @RequestPart yêu cầu application/json)
        val productRequestBody = productJson.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        // 3. Tạo MultipartBody.Part cho Ảnh (Nếu có chọn ảnh)
        val imagePart = param.imageFile?.let { file ->
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", file.name, requestFile)
        }

        return safeApiCall(
            apiCall = { api.upsertProduct(productRequestBody, imagePart) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun deleteProduct(productId: Int): Resource<String> {
        return safeApiCall(
            apiCall = { api.deleteProduct(productId) },
            mapper = { it ?: "Xóa sản phẩm thành công" }
        )
    }
}