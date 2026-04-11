package com.example.billiard.data.mapper

import com.example.billiard.data.remote.dto.request.CreateCategoryRequest
import com.example.billiard.data.remote.dto.response.CategoryDto
import com.example.billiard.domain.model.Category
import com.example.billiard.domain.model.CategoryType
import com.example.billiard.domain.request.CreateCategoryParam

fun CategoryDto.toDomain(): Category {
    // Map an toàn String từ API sang Enum của Domain
    val parsedType = try {
        if (!this.type.isNullOrBlank()) CategoryType.valueOf(this.type.uppercase()) else CategoryType.UNKNOWN
    } catch (e: Exception) {
        CategoryType.UNKNOWN
    }

    return Category(
        id = this.id,
        categoryName = this.categoryName.orEmpty(),
        type = parsedType,
        isDeleted = this.deleted ?: false
    )
}

fun CreateCategoryParam.toDto(): CreateCategoryRequest {
    return CreateCategoryRequest(
        categoryName = this.categoryName,
        type = this.type.name // Chuyển Enum thành String ("RENTAL" hoặc "RETAIL")
    )
}