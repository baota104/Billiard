package com.example.billiard.domain.repository

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.OrderDetail
import com.example.billiard.domain.request.CreateOrderDetailParam

interface OrderDetailRepository {
    // Gọi món (Truyền Request vào, trả về món vừa được tạo)
    suspend fun createOrderDetail(param: CreateOrderDetailParam): Resource<OrderDetail>

    // Hủy món (Truyền ID món vào, trả về Unit - tức là không cần dữ liệu, chỉ cần biết thành công hay thất bại)
    suspend fun deleteOrderDetail(id: Int): Resource<Unit>
}