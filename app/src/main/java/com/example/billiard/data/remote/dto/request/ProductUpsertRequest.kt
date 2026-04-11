package com.example.billiard.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class ProductUpsertRequest(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("sellingPrice") val sellingPrice: Double,
    @SerializedName("importPrice") val importPrice: Double,
    @SerializedName("initStock") val initStock: Int,
    @SerializedName("imageId") val imageId: Int,
    @SerializedName("categoryId") val categoryId: Int
)