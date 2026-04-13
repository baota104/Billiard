package com.example.billiard.data.mapper

import com.example.billiard.data.remote.dto.request.CreateBankRequest
import com.example.billiard.data.remote.dto.request.UpdateBankRequest
import com.example.billiard.data.remote.dto.response.BankDto
import com.example.billiard.data.remote.dto.response.VietQrDto
import com.example.billiard.domain.model.Bank
import com.example.billiard.domain.model.CreateBankParam
import com.example.billiard.domain.model.UpdateBankParam
import com.example.billiard.domain.model.VietQr

fun BankDto.toDomain(): Bank {
    return Bank(
        id = this.id ?: 0,
        bankBin = this.bankBin.orEmpty(),
        bankAccountNo = this.bankAccountNo.orEmpty(),
        bankAccountName = this.bankAccountName.orEmpty(),
        bankStatus = this.bankStatus ?: false,
        bankName = this.bankName.orEmpty(),
        bankShortName = this.bankShortName.orEmpty(),
        bankLogo = this.bankLogo.orEmpty()
    )
}

fun VietQrDto.toDomain(): VietQr {
    return VietQr(
        qrCode = this.qrCode.orEmpty(),
        qrImageUrl = this.qrImageUrl.orEmpty()
    )
}

fun CreateBankParam.toDto(): CreateBankRequest {
    return CreateBankRequest(
        bankBin = this.bankBin,
        bankAccountNo = this.bankAccountNo,
        bankAccountName = this.bankAccountName,
        bankStatus = this.bankStatus,
        bankName = this.bankName,
        bankShortName = this.bankShortName,
        bankLogo = this.bankLogo
    )
}

fun UpdateBankParam.toDto(): UpdateBankRequest {
    return UpdateBankRequest(
        id = this.id,
        bankBin = this.bankBin,
        bankAccountNo = this.bankAccountNo,
        bankAccountName = this.bankAccountName,
        bankStatus = this.bankStatus,
        employeeId = this.employeeId
    )
}