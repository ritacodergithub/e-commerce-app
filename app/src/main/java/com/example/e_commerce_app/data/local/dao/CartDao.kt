package com.example.e_commerce_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.e_commerce_app.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM cart ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<CartItemEntity>>

    @Query("SELECT IFNULL(SUM(quantity), 0) FROM cart")
    fun observeCount(): Flow<Int>

    @Query("SELECT * FROM cart WHERE productId = :id")
    suspend fun byId(id: Int): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CartItemEntity)

    @Query("DELETE FROM cart WHERE productId = :id")
    suspend fun remove(id: Int)

    @Query("DELETE FROM cart")
    suspend fun clear()
}