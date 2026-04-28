package com.example.e_commerce_app.data.repository

import com.example.e_commerce_app.data.local.dao.ProductDao
import com.example.e_commerce_app.data.local.dao.WishlistDao
import com.example.e_commerce_app.data.local.entity.WishlistEntity
import com.example.e_commerce_app.data.toDomain
import com.example.e_commerce_app.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WishlistRepository @Inject constructor(
    private val wishlistDao: WishlistDao,
    private val productDao: ProductDao
) {

    fun observeWishlist(): Flow<List<Product>> =
        wishlistDao.observeAll().combine(productDao.observeAll()) { rows, products ->
            val byId = products.associateBy { it.id }
            rows.mapNotNull { byId[it.productId]?.toDomain() }
        }

    fun observeIds(): Flow<Set<Int>> =
        wishlistDao.observeIds().map { it.toSet() }

    fun observeContains(productId: Int): Flow<Boolean> =
        wishlistDao.observeContains(productId)

    suspend fun toggle(productId: Int): Boolean {
        val isPresent = wishlistDao.contains(productId)
        if (isPresent) {
            wishlistDao.remove(productId)
        } else {
            wishlistDao.add(
                WishlistEntity(productId = productId, addedAt = System.currentTimeMillis())
            )
        }
        return !isPresent
    }

    suspend fun add(productId: Int) {
        wishlistDao.add(
            WishlistEntity(productId = productId, addedAt = System.currentTimeMillis())
        )
    }

    suspend fun remove(productId: Int) = wishlistDao.remove(productId)
}