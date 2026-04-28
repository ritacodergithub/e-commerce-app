package com.example.e_commerce_app.data.repository

import com.example.e_commerce_app.data.local.dao.OrderDao
import com.example.e_commerce_app.data.local.entity.OrderEntity
import com.example.e_commerce_app.data.local.entity.OrderLineEntity
import com.example.e_commerce_app.data.toDomain
import com.example.e_commerce_app.domain.model.CartItem
import com.example.e_commerce_app.domain.model.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val orderDao: OrderDao
) {

    fun observeOrders(): Flow<List<Order>> =
        orderDao.observeOrders().map { rows ->
            rows.map { it.toDomain(emptyList()) }
        }

    suspend fun orderDetails(orderId: String): List<com.example.e_commerce_app.domain.model.OrderLine> =
        orderDao.linesFor(orderId).map { it.toDomain() }

    suspend fun placeOrder(
        items: List<CartItem>,
        subtotal: Double,
        shipping: Double,
        tax: Double,
        total: Double,
        paymentMethod: String,
        shippingAddress: String
    ): String {
        val orderId = "SHP-" + (100000..999999).random()
        val now = System.currentTimeMillis()

        val order = OrderEntity(
            orderId = orderId,
            placedAt = now,
            subtotal = subtotal,
            shipping = shipping,
            tax = tax,
            total = total,
            paymentMethod = paymentMethod,
            shippingAddress = shippingAddress,
            itemCount = items.sumOf { it.quantity }
        )

        val lines = items.map { item ->
            OrderLineEntity(
                orderId = orderId,
                productId = item.product.id,
                title = item.product.title,
                brand = item.product.brand,
                thumbnail = item.product.thumbnail,
                price = item.product.price,
                quantity = item.quantity
            )
        }

        orderDao.insertWithLines(order, lines)
        return orderId
    }
}