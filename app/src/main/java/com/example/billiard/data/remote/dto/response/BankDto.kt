package com.example.billiard.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class BankDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("bankBin") val bankBin: String?,
    @SerializedName("bankAccountNo") val bankAccountNo: String?,
    @SerializedName("bankAccountName") val bankAccountName: String?,
    @SerializedName("bankStatus") val bankStatus: Boolean?,
    @SerializedName("bankName") val bankName: String?,
    @SerializedName("bankShortName") val bankShortName: String?,
    @SerializedName("bankLogo") val bankLogo: String?
)

data class VietQrDto(
    @SerializedName("qrCode") val qrCode: String?,
    @SerializedName("qrImageUrl") val qrImageUrl: String?
)