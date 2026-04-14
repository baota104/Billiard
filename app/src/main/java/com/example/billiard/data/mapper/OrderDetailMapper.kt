package com.example.billiard.data.mapper

import com.example.billiard.data.remote.dto.request.CreateOrderDetailRequest
import com.example.billiard.data.remote.dto.response.OrderDetailDto
import com.example.billiard.domain.model.OrderDetail
import com.example.billiard.domain.request.CreateOrderDetailParam

fun OrderDetailDto.toDomain(): OrderDetail {
    return OrderDetail(
        id = this.id,
        invoiceId = this.invoiceId,
        productId = this.productId,
        productName = this.productName ?: "Sản phẩm không tên",
        price = this.price,
        quantity = this.quantity,
        imageUrl = this.imageUrl
    )
}
fun CreateOrderDetailParam.toRequest(): CreateOrderDetailRequest {
    return CreateOrderDetailRequest(
        invoiceId = this.invoiceId,
        productId = this.productId,
        quantity = this.quantity,
        price = this.price,
//        imageUrl = this.imageUrl,
        note = this.note
    )
}