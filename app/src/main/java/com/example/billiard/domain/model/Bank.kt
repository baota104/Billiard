package com.example.billiard.domain.model

data class Bank(
    val id: Int,
    val bankBin: String,
    val bankAccountNo: String,
    val bankAccountName: String,
    val bankStatus: Boolean,
    val bankName: String,
    val bankShortName: String,
    val bankLogo: String
)

data class VietQr(
    val qrCode: String,
    val qrImageUrl: String
)