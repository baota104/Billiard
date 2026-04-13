package com.example.billiard.domain.model

data class CreateBankParam(
    val bankBin: String,
    val bankAccountNo: String,
    val bankAccountName: String,
    val bankStatus: Boolean,
    val bankName: String,
    val bankShortName: String,
    val bankLogo: String
)