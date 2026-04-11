package com.example.billiard.data.mapper

import com.example.billiard.data.remote.dto.request.CreateVoucherRequest
import com.example.billiard.data.remote.dto.request.UpdateVoucherRequest
import com.example.billiard.data.remote.dto.response.VoucherDto
import com.example.billiard.domain.model.CreateVoucherParam
import com.example.billiard.domain.model.UpdateVoucherParam
import com.example.billiard.domain.model.Voucher

fun VoucherDto.toDomain(): Voucher {
    return Voucher(
        id = this.id ?: 0L,
        code = this.code.orEmpty(),
        voucherType = this.voucherType.orEmpty(),
        value = this.value ?: 0.0,
        source = this.source.orEmpty(),
        status = this.status.orEmpty(),
        startTime = this.startTime.orEmpty(),
        endTime = this.endTime.orEmpty(),
        quantity = this.quantity ?: 0,
        minimumAmount = this.minimumAmount ?: 0.0
    )
}

fun CreateVoucherParam.toDto(): CreateVoucherRequest {
    return CreateVoucherRequest(
        voucherType = this.voucherType,
        value = this.value,
        source = this.source,
        status = this.status,
        startTime = this.startTime,
        endTime = this.endTime,
        quantity = this.quantity,
        minimumAmount = this.minimumAmount,
        maximumValue = this.maximumValue
    )
}

fun UpdateVoucherParam.toDto(): UpdateVoucherRequest {
    return UpdateVoucherRequest(
        status = this.status,
        startTime = this.startTime,
        endTime = this.endTime,
        quantity = this.quantity
    )
}