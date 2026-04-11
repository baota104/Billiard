package com.example.billiard.domain.request

import java.io.File

data class UpsertProductParam(
    val id: Int = 0,
    val name: String,
    val sellingPrice: Double,
    val importPrice: Double,
    val initStock: Int,
    val imageId: Int = 0,
    val categoryId: Int,
    val imageFile: File? = null // File ảnh (chỉ có khi người dùng chọn ảnh mới)
)