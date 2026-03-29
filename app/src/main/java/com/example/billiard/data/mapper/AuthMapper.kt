package com.example.billiard.data.mapper

import com.example.billiard.data.remote.dto.AuthBody
import com.example.billiard.domain.model.UserAuth

fun AuthBody.toDomain() : UserAuth {
    return UserAuth(
        accessToken = this.accessToken,
        refreshToken = this.refreshToken,
        role = this.employee.role,
        fullName = "${this.employee.firstName} ${this.employee.lastName}"
    )
}