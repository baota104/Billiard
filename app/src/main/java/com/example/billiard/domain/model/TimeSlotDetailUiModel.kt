package com.example.billiard.domain.model

data class TimeSlotDetailUiModel(
    val id: String,
    val title: String,       // VD: "Sáng"
    val description: String, // VD: "Khung giờ phổ thông"
    val timeRange: String,   // VD: "08:00 - 17:00"
    val price: Int,
    val slotType: SlotThemeType // Để quy định màu sắc
) {
    val formattedPrice: String get() = "%,dđ".format(price).replace(',', '.')
}

// Bộ Enum lưu trữ mã màu
enum class SlotThemeType(val bgHex: String, val iconTintHex: String) {
    MORNING("#FFF9C4", "#FBC02D"),   // Vàng nhạt - Vàng đậm
    AFTERNOON("#FFE0B2", "#F57C00"), // Cam nhạt - Cam đậm
    NIGHT("#E8EAF6", "#5C6BC0"),     // Tím nhạt - Tím đậm
    WEEKEND("#FCE4EC", "#F06292")    // Hồng nhạt - Hồng đậm
}