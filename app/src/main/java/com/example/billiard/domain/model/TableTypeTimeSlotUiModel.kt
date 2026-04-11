package com.example.billiard.domain.model

data class TableTypeTimeSlotUiModel(
    val id: String,
    val name: String,
    val badgeText: String,
    val badgeColorHex: String, 
    val imageResId: String, // Thay imageUrl bằng imageResId vì bạn nói ảnh có sẵn trong thư mục drawable
    val configCount: Int
)