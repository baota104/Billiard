package com.example.billiard.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class UpdateBankRequest(
    @SerializedName("id") val id: Int,
    @SerializedName("bankBin") val bankBin: String,
    @SerializedName("bankAccountNo") val bankAccountNo: String,
    @SerializedName("bankAccountName") val bankAccountName: String,
    @SerializedName("bankStatus") val bankStatus: Boolean,
    @SerializedName("employeeId") val employeeId: Int
)