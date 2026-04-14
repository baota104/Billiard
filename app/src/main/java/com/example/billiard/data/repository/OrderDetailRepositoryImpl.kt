package com.example.billiard.data.repository

import android.util.Log
import com.example.billiard.core.network.Resource
import com.example.billiard.data.mapper.toDomain
import com.example.billiard.data.mapper.toRequest
import com.example.billiard.data.remote.api.OrderDetailApiService
import com.example.billiard.data.remote.dto.request.CreateOrderDetailRequest
import com.example.billiard.domain.model.OrderDetail
import com.example.billiard.domain.repository.OrderDetailRepository
import com.example.billiard.domain.request.CreateOrderDetailParam
import javax.inject.Inject

class OrderDetailRepositoryImpl @Inject constructor(
    private val api: OrderDetailApiService
) : OrderDetailRepository {

    override suspend fun createOrderDetail(param: CreateOrderDetailParam): Resource<OrderDetail> {
        return try {
            val request = param.toRequest()
            val response = api.createOrderDetail(request)

            Log.d("OrderDetailRepo", "HTTP Code: ${response.code()}")

            // isSuccessful sẽ tự động nhận diện cả 200 (OK) và 201 (Created) là thành công
            if (response.isSuccessful) {
                val orderDetailDto = response.body() // Bây giờ body() chính là OrderDetailDto luôn

                if (orderDetailDto != null) {
                    Resource.Success(orderDetailDto.toDomain())
                } else {
                    // Đề phòng trường hợp API trả về 201 nhưng body rỗng (chỉ báo thành công)
                    Log.e("OrderDetailRepo", "Thêm thành công nhưng Server không trả về data món ăn.")
                    // Tạo một object ảo để luồng code vẫn chạy tiếp báo Success cho UI
                    val fallbackDomain = OrderDetail(
                        id = 0, invoiceId = param.invoiceId, productId = param.productId,
                        productName = "Sản phẩm", price = param.price, quantity = param.quantity, imageUrl = null
                    )
                    Resource.Success(fallbackDomain)
                }
            } else {
                val errorString = response.errorBody()?.string()
                Log.e("OrderDetailRepo", "Create failed HTTP ${response.code()}: $errorString")
                Resource.Error("Lỗi thêm món: HTTP ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("OrderDetailRepo", "Create Exception: ${e.message}", e)
            Resource.Error("Lỗi kết nối: Không thể gọi Server", e)
        }
    }

    override suspend fun deleteOrderDetail(id: Int): Resource<Unit> {
        return try {
            val response = api.deleteOrderDetail(id)

            if (response.isSuccessful && response.body()?.status == 200) {
                Resource.Success(Unit)
            } else {
                Log.e("OrderDetailRepo", "Delete failed: ${response.errorBody()?.string()}")
                Resource.Error(response.body()?.message ?: "Lỗi hủy món")
            }
        } catch (e: Exception) {
            Log.e("OrderDetailRepo", "Delete Exception: ${e.message}", e)
            Resource.Error("Lỗi kết nối: Không thể hủy món", e)
        }
    }
}