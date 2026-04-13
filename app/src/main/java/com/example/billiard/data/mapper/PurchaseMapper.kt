package com.example.billiard.data.mapper

import com.example.billiard.data.remote.dto.request.CreatePurchaseInvoiceRequest
import com.example.billiard.data.remote.dto.request.PurchaseDetailRequest
import com.example.billiard.data.remote.dto.response.PurchaseDetailDto
import com.example.billiard.data.remote.dto.response.PurchaseHistoryDto
import com.example.billiard.data.remote.dto.response.PurchaseInvoiceDto
import com.example.billiard.domain.model.CreatePurchaseParam
import com.example.billiard.domain.model.PurchaseDetail
import com.example.billiard.domain.model.PurchaseHistory
import com.example.billiard.domain.model.PurchaseInvoice

fun PurchaseInvoiceDto.toDomain(): PurchaseInvoice {
    return PurchaseInvoice(
        id = this.id ?: 0,
        totalAmount = this.totalAmount ?: 0.0,
        importDate = this.importDate.orEmpty(),
        employeeId = this.employeeId ?: 0,
        employeeName = this.employeeName.orEmpty(),
        details = this.details?.map { it.toDomain() } ?: emptyList()
    )
}

fun PurchaseDetailDto.toDomain(): PurchaseDetail {
    return PurchaseDetail(
        id = this.id ?: 0,
        productId = this.productId ?: 0,
        productName = this.productName.orEmpty(),
        quantity = this.quantity ?: 0,
        importPrice = this.importPrice ?: 0.0,
        subTotal = this.subTotal ?: 0.0,
        imageUrl = this.imageUrl ?:""
    )
}

fun PurchaseHistoryDto.toDomain(): PurchaseHistory {
    return PurchaseHistory(
        purchaseId = this.purchaseId ?: 0,
        totalPrice = this.totalPrice ?: 0.0,
        purchaseDate = this.purchaseDate.orEmpty(),
        employeeName = this.employeeName.orEmpty()
    )
}

fun CreatePurchaseParam.toDto(): CreatePurchaseInvoiceRequest {
    return CreatePurchaseInvoiceRequest(
        details = this.details.map { detail ->
            PurchaseDetailRequest(
                productId = detail.productId,
                quantity = detail.quantity,
                importPrice = detail.importPrice
            )
        }
    )
}