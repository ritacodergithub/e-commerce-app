package com.example.e_commerce_app.domain.model

data class CartItem(
    val product: Product,
    val quantity: Int
) {
    val lineTotal: Double get() = product.price * quantity
}