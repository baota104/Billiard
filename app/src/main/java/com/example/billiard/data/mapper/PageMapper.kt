package com.example.billiard.data.mapper

import com.example.billiard.data.remote.dto.response.PageDto
import com.example.billiard.domain.model.PageData

// Hàm extension dùng chung cho tất cả các PageDto (Employee, Table, Order...)
fun <T, R> PageDto<T>.toDomain(mapper: (T) -> R): PageData<R> {
    return PageData(
        content = this.content.map { mapper(it) },
        totalElements = this.totalElements,
        totalPages = this.totalPages,
        currentPage = this.number,
        isLastPage = this.last ?: true
    )
}