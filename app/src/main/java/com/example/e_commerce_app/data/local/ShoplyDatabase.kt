package com.example.e_commerce_app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.e_commerce_app.data.local.dao.CartDao
import com.example.e_commerce_app.data.local.dao.OrderDao
import com.example.e_commerce_app.data.local.dao.ProductDao
import com.example.e_commerce_app.data.local.dao.WishlistDao
import com.example.e_commerce_app.data.local.entity.CartItemEntity
import com.example.e_commerce_app.data.local.entity.OrderEntity
import com.example.e_commerce_app.data.local.entity.OrderLineEntity
import com.example.e_commerce_app.data.local.entity.ProductEntity
import com.example.e_commerce_app.data.local.entity.WishlistEntity

@Database(
    entities = [
        ProductEntity::class,
        CartItemEntity::class,
        WishlistEntity::class,
        OrderEntity::class,
        OrderLineEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ShoplyDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun orderDao(): OrderDao

    companion object {
        const val NAME = "shoply.db"
    }
}