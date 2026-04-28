package com.example.e_commerce_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.e_commerce_app.data.local.entity.WishlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {

    @Query("SELECT * FROM wishlist ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<WishlistEntity>>

    @Query("SELECT productId FROM wishlist")
    fun observeIds(): Flow<List<Int>>

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist WHERE productId = :id)")
    fun observeContains(id: Int): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist WHERE productId = :id)")
    suspend fun contains(id: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(item: WishlistEntity)

    @Query("DELETE FROM wishlist WHERE productId = :id")
    suspend fun remove(id: Int)

    @Query("DELETE FROM wishlist")
    suspend fun clear()
}