package com.example.billiard.domain.request

import com.example.billiard.domain.model.CategoryType

data class CreateCategoryParam(
    val categoryName: String,
    val type: CategoryType
)