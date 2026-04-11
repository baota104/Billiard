package com.example.billiard.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class CreateTableRequest(
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String
)