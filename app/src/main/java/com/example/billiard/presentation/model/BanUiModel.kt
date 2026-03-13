package com.example.billiard.presentation.model

import com.example.billiard.domain.model.Ban

data class BanUiModel (
    val id: String,           // Giữ lại để Adapter phân biệt các item
    val ten: String,  // Tên hiển thị
    val trangthai: String,// Trạng thái hiển thị
    val theloai: String, // Tên loại bàn
    val imageUrl: String?
)
fun Ban.toUiModel(): BanUiModel {
    return BanUiModel(
        id = this.id,
        ten = this.tenBan,
        trangthai = this.trangThai,
        theloai = this.tenLoaiBan.uppercase(),
        imageUrl = this.imageUrl
    )
}

