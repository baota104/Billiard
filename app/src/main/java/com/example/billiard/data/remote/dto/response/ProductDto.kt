package com.example.billiard.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("price") val price: Double?,
    @SerializedName("stock") val stock: Int?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("categoryName") val categoryName: String?
)