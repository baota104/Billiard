package com.example.billiard.domain.model

import java.io.File

data class UpdateTableParam(
    val id: Int,
    val name: String,
    val status: String,
    val tableType: String,
    val imageFile: File? = null
)