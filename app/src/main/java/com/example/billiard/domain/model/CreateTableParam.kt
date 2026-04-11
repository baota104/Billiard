package com.example.billiard.domain.model

import java.io.File

data class CreateTableParam(
    val name: String,
    val status: String,
    val tableType: String,
    val imageFile: File? = null // Bổ sung tham số để đính kèm ảnh (nếu có)
)