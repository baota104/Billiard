package com.example.billiard.domain.model

data class BanUiModel (
    val id: String,
    val name: String,
    val price: String,
    val type: String, // "Bida Lỗ" hoặc "Snooker"
    val status: TableStatus,
    val timePlaying: String? = null // Ví dụ: "01:25:00", nếu trống thì là null
)
enum class TableStatus {
    PLAYING, EMPTY,MAINTAIN
}
//fun Ban.toUiModel(): BanUiModel {
//    return BanUiModel(
//        id = this.id,
//        ten = this.tenBan,
//        trangthai = this.trangThai,
//        theloai = this.tenLoaiBan.uppercase(),
//        imageUrl = this.imageUrl
//    )
//}

