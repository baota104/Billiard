package com.example.billiard.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class CreateBankRequest(
    @SerializedName("bankBin") val bankBin: String,
    @SerializedName("bankAccountNo") val bankAccountNo: String,
    @SerializedName("bankAccountName") val bankAccountName: String,
    @SerializedName("bankStatus") val bankStatus: Boolean,
    @SerializedName("bankName") val bankName: String,
    @SerializedName("bankShortName") val bankShortName: String,
    @SerializedName("bankLogo") val bankLogo: String
)