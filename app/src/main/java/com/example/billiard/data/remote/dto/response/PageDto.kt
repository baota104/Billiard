package com.example.billiard.data.remote.dto.response

import com.google.gson.annotations.SerializedName

/**
 * Wrapper DTO hứng Pagination của Spring Boot trả về, dùng chung cho toàn bộ Project
 */
data class PageDto<T>(
    @SerializedName("content") val content: List<T>,
    @SerializedName("totalElements") val totalElements: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("size") val size: Int,
    @SerializedName("number") val number: Int,
    @SerializedName("last") val last: Boolean? // Cần cho tính năng Load More của Table
)