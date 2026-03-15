package com.example.billiard.domain.model
data class OrderServiceUiModel(
    val orderId: String,
    val serviceId: String,
    val name: String,          // Tên món (Bia Heineken, Thuê Gậy VIP)
    val category: String,      // Phân loại (Đồ uống, Dịch vụ thuê...)
    val imageUrl: String,
    val quantity: Int,
    val unitPrice: Int,

    val startTime: String?,
    val endTime: String?
) {
    // 1. Nhận diện loại dịch vụ
    val isRentableService: Boolean
        get() = startTime != null

    // 2. Tự động format dòng Mô tả (Text màu xám nhạt dưới tên món)
    val displayDescription: String
        get() {
            return if (isRentableService) {
                // Nếu là dịch vụ thuê -> Hiển thị Giờ bắt đầu - Giờ kết thúc
                val endStr = endTime ?: "Đang hoạt động"
                "$category • $startTime - $endStr"
                // Kết quả: "Dịch vụ thuê • 14:30 - Đang hoạt động"
            } else {
                // Nếu là món ăn bình thường -> Hiển thị danh mục
                category
                // Kết quả: "Đồ uống • 330ml"
            }
        }

    val displayQuantity: String
        get() {
            return if (isRentableService) {
                // Dịch vụ thuê thường không cần hiển thị "x1", hoặc hiển thị số giờ
                // Ở đây mình trả về chuỗi rỗng để ẩn nó đi, hoặc bạn có thể custom
                if (quantity > 1) "x$quantity" else ""
            } else {
                "x$quantity"
            }
        }

    // 4. Tính toán Thành tiền
    val totalPrice: Int
        get() {
            return if (isRentableService) {
                // Nếu backend trả về sẵn tổng tiền thì xài luôn.
                // Nếu không, bạn phải tính: (Thời gian hiện tại - startTime) * unitPrice
                quantity * unitPrice
            } else {
                quantity * unitPrice
            }
        }

    val formattedTotalPrice: String
        get() = "%,dđ".format(totalPrice).replace(',', '.')
}