package com.example.billiard.domain.model

// Khai báo Enum để kiểm soát chặt chẽ Type của Category theo yêu cầu của Backend
enum class CategoryType {
    RENTAL, RETAIL, UNKNOWN
}

data class Category(
    val id: Int,
    val categoryName: String,
    val type: CategoryType,
    val isDeleted: Boolean
)