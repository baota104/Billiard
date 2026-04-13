package com.example.billiard.domain.model

data class UpdateBankParam(
    val id: Int,
    val bankBin: String,
    val bankAccountNo: String,
    val bankAccountName: String,
    val bankStatus: Boolean,
    val employeeId: Int
)