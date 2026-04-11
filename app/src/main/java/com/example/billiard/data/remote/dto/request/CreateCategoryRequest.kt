package com.example.billiard.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class CreateCategoryRequest(
    @SerializedName("categoryName") val categoryName: String,
    @SerializedName("type") val type: String
)