package com.example.billiard.domain.model

data class TableTypeTimeSlotUiModel(
    val id: String,
    val name: String,
    val badgeText: String,
    val badgeColorHex: String, // VD: "#2962FF"
    val imageUrl: String,      // Link ảnh
    val configCount: Int       // 0 là Chưa cấu hình
)