package com.example.billiard.domain.model

data class BankUiModel(
    val id: Int,
    val bankName: String,         // Map từ bank_name
    val accountHolder: String,    // Map từ bank_account_name
    val accountNumber: String,    // Map từ bank_account_no
    val logoUrl: String? = null,  // Map từ bank_logo
    var isDefault: Boolean = false // Map từ bank_status (ví dụ: status == 1)
) {
    // Tiện ích: Format số tài khoản thêm khoảng trắng cho dễ nhìn (VD: 8888 1234 5678)
    val formattedAccountNumber: String
        get() = accountNumber.chunked(4).joinToString("  ")
}