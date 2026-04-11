package com.example.billiard.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class GetTableRequest(
    @SerializedName("page") val page: Int,
    @SerializedName("size") val size: Int,
    @SerializedName("sort") val sort: List<String>? = listOf("id,asc")
)