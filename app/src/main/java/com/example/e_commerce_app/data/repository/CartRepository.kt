package com.example.e_commerce_app.data.repository

import android.util.Log
import com.example.e_commerce_app.data.local.dao.CartDao
import com.example.e_commerce_app.data.local.dao.ProductDao
import com.example.e_commerce_app.data.local.entity.CartItemEntity
import com.example.e_commerce_app.data.toDomain
import com.example.e_commerce_app.domain.model.CartItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val cartDao: CartDao,
    private val productDao: ProductDao
) {

    fun observeCart(): Flow<List<CartItem>> =
        cartDao.observeAll().combine(productDao.observeAll()) { rows, products ->
            val byId = products.associateBy { it.id }
            Log.e("TAG", "observeCart: $byId", )
            rows.mapNotNull { row ->
                val product = byId[row.productId]?.toDomain() ?: return@mapNotNull null
                CartItem(product = product, quantity = row.quantity)
            }
        }

    fun observeCount(): Flow<Int> = cartDao.observeCount()

    suspend fun add(productId: Int, quantity: Int = 1) {
        val existing = cartDao.byId(productId)
        cartDao.upsert(
            CartItemEntity(
                productId = productId,
                quantity = (existing?.quantity ?: 0) + quantity,
                addedAt = existing?.addedAt ?: System.currentTimeMillis()
            )
        )
    }

    suspend fun setQuantity(productId: Int, quantity: Int) {
        if (quantity <= 0) {
            cartDao.remove(productId)
        } else {
            val existing = cartDao.byId(productId)
            cartDao.upsert(
                CartItemEntity(
                    productId = productId,
                    quantity = quantity,
                    addedAt = existing?.addedAt ?: System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun remove(productId: Int) = cartDao.remove(productId)

    suspend fun clear() = cartDao.clear()
}