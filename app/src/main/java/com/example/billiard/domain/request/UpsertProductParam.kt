package com.example.billiard.domain.request

import java.io.File

data class UpsertProductParam(
    val id: Int,
    val name: String,
    val sellingPrice: Double,
    val importPrice: Double,
    val initStock: Int,
    val categoryId: Int,
    val imageFile: File? = null
)