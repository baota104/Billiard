package com.example.billiard.data.mapper

import com.example.billiard.data.remote.dto.response.ActiveInvoiceDto
import com.example.billiard.data.remote.dto.response.DashboardTableDto
import com.example.billiard.domain.model.ActiveInvoice
import com.example.billiard.domain.model.DashboardTable

fun DashboardTableDto.toDomain(): DashboardTable {
    return DashboardTable(
        id = this.id ?: 0,
        name = this.name.orEmpty(),
        status = this.status.orEmpty(),
        tableType = this.tableType.orEmpty(),
        imageUrl = this.imageUrl.orEmpty(), // Lấy ảnh từ Backend (nếu có)
        activeInvoice = this.activeInvoice?.toDomain()
    )
}

fun ActiveInvoiceDto.toDomain(): ActiveInvoice {
    return ActiveInvoice(
        id = this.id ?: 0,
        startAt = this.startAt.orEmpty(),
        employeeName = this.employeeName.orEmpty()
    )
}