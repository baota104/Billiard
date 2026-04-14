package com.example.billiard.data.mapper

import com.example.billiard.data.remote.dto.request.CreateInvoiceRequest
import com.example.billiard.data.remote.dto.request.UpdateInvoiceRequest
import com.example.billiard.data.remote.dto.response.InvoiceDto
import com.example.billiard.domain.model.CreateInvoiceParam
import com.example.billiard.domain.model.Invoice
import com.example.billiard.domain.model.UpdateInvoiceParam
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Đã cập nhật format thêm SSSZ để khớp 100% với Spring Boot LocalDateTime khi có JsonFormat
private fun Date?.toIsoString(): String? {
    if (this == null) return null
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(this)
}

fun InvoiceDto.toDomain(): Invoice {
    return Invoice(
        id = this.id ?: 0,
        startTime = this.startTime.orEmpty(),
        endTime = this.endTime.orEmpty(),
        status = this.status.orEmpty(),
        paymentMethod = this.paymentMethod.orEmpty(),
        serviceAmount = this.serviceAmount ?: 0.0,
        productAmount = this.productAmount ?: 0.0,
        taxAmount = this.taxAmount ?: 0.0,
        totalAmount = this.totalAmount ?: 0.0,
        voucherId = this.voucherId ?: 0,
        voucherCode = this.voucherCode.orEmpty(),
        employeeId = this.employeeId ?: 0,
        employeeName = this.employeeName.orEmpty(),
        billiardTableId = this.billiardTableId ?: 0,
        billiardTableName = this.billiardTableName.orEmpty(),
        orderDetails = this.details?.map { it.toDomain() }
    )
}

fun CreateInvoiceParam.toDto(): CreateInvoiceRequest {
    return CreateInvoiceRequest(
        startTime = this.startTime.toIsoString(),
        endTime = this.endTime.toIsoString(),
        paymentMethod = this.paymentMethod,
        serviceAmount = this.serviceAmount,
        productAmount = this.productAmount,
        taxAmount = this.taxAmount,
        totalAmount = this.totalAmount,
        voucherId = this.voucherId,
        employeeId = this.employeeId,
        billiardTableId = this.billiardTableId
    )
}

fun UpdateInvoiceParam.toDto(): UpdateInvoiceRequest {
    return UpdateInvoiceRequest(
        id = this.id,
        startTime = this.startTime.toIsoString(),
        endTime = this.endTime.toIsoString(),
        status = this.status,
        paymentMethod = this.paymentMethod,
        serviceAmount = this.serviceAmount,
        productAmount = this.productAmount,
        taxAmount = this.taxAmount,
        totalAmount = this.totalAmount,
        voucherId = this.voucherId,
        employeeId = this.employeeId,
        billiardTableId = this.billiardTableId
    )
}