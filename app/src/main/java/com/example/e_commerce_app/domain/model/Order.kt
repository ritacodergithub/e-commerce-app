package com.example.e_commerce_app.domain.model

data class Order(
    val orderId: String,
    val placedAt: Long,
    val subtotal: Double,
    val shipping: Double,
    val tax: Double,
    val total: Double,
    val paymentMethod: String,
    val shippingAddress: String,
    val itemCount: Int,
    val lines: List<OrderLine> = emptyList()
)

data class OrderLine(
    val productId: Int,
    val title: String,
    val brand: String,
    val thumbnail: String,
    val price: Double,
    val quantity: Int
) {
    val lineTotal: Double get() = price * quantity
}