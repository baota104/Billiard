package com.example.billiard.data.mapper

import com.example.billiard.data.remote.dto.request.CreatePriceListRequest
import com.example.billiard.data.remote.dto.request.UpdatePriceListRequest
import com.example.billiard.data.remote.dto.response.PriceListDto
import com.example.billiard.domain.model.CreatePriceListParam
import com.example.billiard.domain.model.PriceList
import com.example.billiard.domain.model.UpdatePriceListParam

fun PriceListDto.toDomain(): PriceList {
    return PriceList(
        id = this.id ?: 0,
        startTime = this.startTime.orEmpty(),
        endTime = this.endTime.orEmpty(),
        unitPrice = this.unitPrice ?: 0.0,
        tableType = this.tableType.orEmpty()
    )
}

fun CreatePriceListParam.toDto(): CreatePriceListRequest {
    return CreatePriceListRequest(
        startTime = this.startTime,
        endTime = this.endTime,
        unitPrice = this.unitPrice,
        tableType = this.tableType
    )
}

fun UpdatePriceListParam.toDto(): UpdatePriceListRequest {
    return UpdatePriceListRequest(
        id = this.id,
        startTime = this.startTime,
        endTime = this.endTime,
        unitPrice = this.unitPrice,
        tableType = this.tableType
    )
}