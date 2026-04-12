package com.example.billiard.data.mapper

import com.example.billiard.data.remote.dto.request.ProductUpsertRequest
import com.example.billiard.data.remote.dto.response.ProductDto
import com.example.billiard.domain.model.Product
import com.example.billiard.domain.request.UpsertProductParam

fun ProductDto.toDomain(): Product {
    return Product(
        id = this.id,
        name = this.name.orEmpty(),
        sellingPrice = this.sellingPrice ?: 0.0,
        stock = this.stock ?: 0,
        imageUrl = this.imageUrl.orEmpty(),
        categoryName = this.categoryName.orEmpty()
    )
}

fun UpsertProductParam.toDto(): ProductUpsertRequest {
    return ProductUpsertRequest(
        id = this.id,
        name = this.name,
        sellingPrice = this.sellingPrice,
        importPrice = this.importPrice,
        initStock = this.initStock,
        categoryId = this.categoryId,
        imageId = 0 // Giữ lại tạm nếu cần
    )
}