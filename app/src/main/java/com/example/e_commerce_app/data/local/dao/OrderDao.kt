package com.example.e_commerce_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.e_commerce_app.data.local.entity.OrderEntity
import com.example.e_commerce_app.data.local.entity.OrderLineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Query("SELECT * FROM orders ORDER BY placedAt DESC")
    fun observeOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM order_lines WHERE orderId = :orderId")
    suspend fun linesFor(orderId: String): List<OrderLineEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLines(lines: List<OrderLineEntity>)

    @Transaction
    suspend fun insertWithLines(order: OrderEntity, lines: List<OrderLineEntity>) {
        insertOrder(order)
        insertLines(lines)
    }
}