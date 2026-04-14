package com.example.billiard.domain.usecase.orderdetail

import com.example.billiard.core.network.Resource
import com.example.billiard.data.remote.dto.request.CreateOrderDetailRequest
import com.example.billiard.domain.model.OrderDetail
import com.example.billiard.domain.repository.OrderDetailRepository
import com.example.billiard.domain.request.CreateOrderDetailParam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateOrderDetailUseCase @Inject constructor(
    private val repository: OrderDetailRepository
) {
    // Truyền request gọi món vào, trả về Flow chứa trạng thái và Dữ liệu món ăn
    operator fun invoke(param: CreateOrderDetailParam): Flow<Resource<OrderDetail>> = flow {
        emit(Resource.Loading) // Phát trạng thái Loading để UI quay vòng vòng
        emit(repository.createOrderDetail(param)) // Gọi API và phát kết quả (Success/Error)
    }
}
class DeleteOrderDetailUseCase @Inject constructor(
    private val repository: OrderDetailRepository
) {
    // Truyền ID của món cần xóa vào, trả về Unit (Không cần cục Data, chỉ cần biết thành công hay không)
    operator fun invoke(id: Int): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        emit(repository.deleteOrderDetail(id))
    }
}