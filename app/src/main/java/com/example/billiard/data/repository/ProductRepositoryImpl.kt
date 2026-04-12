package com.example.billiard.data.repository

import com.example.billiard.core.base.BaseRepository
import com.example.billiard.core.network.Resource
import com.example.billiard.data.mapper.toDomain
import com.example.billiard.data.remote.api.ProductApiService
import com.example.billiard.domain.model.PageData
import com.example.billiard.domain.model.Product
import com.example.billiard.domain.repository.ProductRepository
import com.example.billiard.domain.request.UpsertProductParam
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApiService
) : BaseRepository(), ProductRepository {

    override suspend fun getAllProducts(page: Int, size: Int): Resource<PageData<Product>> {
        return safeApiCall(
            apiCall = { api.getAllProducts(page, size, listOf("id,asc")) },
            mapper = { pageDto ->
                PageData(
                    content = pageDto.content.map { it.toDomain() },
                    totalElements = pageDto.totalElements,
                    totalPages = pageDto.totalPages,
                    currentPage = pageDto.number,
                    isLastPage = pageDto.last ?: true
                )
            }
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
        val idBody = param.id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val nameBody = param.name.toRequestBody("text/plain".toMediaTypeOrNull())
        val sellingPriceBody = param.sellingPrice.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val importPriceBody = param.importPrice.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val initStockBody = param.initStock.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val categoryIdBody = param.categoryId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        // Trong Swagger ghi "image": string($binary)
        val imagePart = param.imageFile?.let { file ->
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", file.name, requestFile) // Key là "image"
        }

        return safeApiCall(
            apiCall = {
                api.upsertProduct(
                    id = idBody,
                    name = nameBody,
                    sellingPrice = sellingPriceBody,
                    importPrice = importPriceBody,
                    initStock = initStockBody,
                    categoryId = categoryIdBody,
                    image = imagePart
                )
            },
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