package com.example.billiard.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class CategoryDto(
    @SerializedName("id") val id: Int,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?,
    @SerializedName("deleted") val deleted: Boolean?,
    @SerializedName("categoryName") val categoryName: String?,
    @SerializedName("type") val type: String?
)