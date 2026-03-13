package com.example.billiard.domain.model

data class Ban(
    val id: String,
    val tenBan: String,
    val trangThai: String,
    val idLoaiBan: String,
    val tenLoaiBan: String,
    val imageUrl: String?
)