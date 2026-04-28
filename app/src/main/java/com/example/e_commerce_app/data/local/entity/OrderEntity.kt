package com.example.e_commerce_app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val placedAt: Long,
    val subtotal: Double,
    val shipping: Double,
    val tax: Double,
    val total: Double,
    val paymentMethod: String,
    val shippingAddress: String,
    val itemCount: Int
)

@Entity(
    tableName = "order_lines",
    primaryKeys = ["orderId", "productId"],
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["orderId"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("orderId")]
)
data class OrderLineEntity(
    val orderId: String,
    val productId: Int,
    val title: String,
    val brand: String,
    val thumbnail: String,
    val price: Double,
    val quantity: Int
)