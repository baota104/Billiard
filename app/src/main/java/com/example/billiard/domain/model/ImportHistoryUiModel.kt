package com.example.billiard.domain.model

data class ImportHistoryUiModel(
    val id: Int,
    val importDate: String,     // Map từ trường 'created_at' (VD: "25/10/2023 14:30")
    val totalAmount: Double,    // Map từ trường 'total_amount'
    val employeeName: String    // Lấy từ bảng Employee thông qua 'Employeeid'
) {
    // Tự động tạo mã phiếu (VD: id = 5 -> #PN005)
    val receiptCode: String
        get() = "#PN%03d".format(id)

    // Tự động format tiền tệ (VD: 2500000 -> 2,500,000đ)
    val formattedTotalAmount: String
        get() = "%,.0fđ".format(totalAmount).replace(',', '.')
}