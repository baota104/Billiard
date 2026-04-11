package com.example.billiard.domain.model

data class PageData<T>(
    val content: List<T>,
    val totalElements: Int,
    val totalPages: Int,
    val currentPage: Int,
    val isLastPage: Boolean
)