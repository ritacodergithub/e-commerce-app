package com.example.e_commerce_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartItemEntity(
    @PrimaryKey val productId: Int,
    val quantity: Int,
    val addedAt: Long
)