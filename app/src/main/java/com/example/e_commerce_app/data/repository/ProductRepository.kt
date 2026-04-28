package com.example.e_commerce_app.data.repository

import com.example.e_commerce_app.data.local.dao.ProductDao
import com.example.e_commerce_app.data.remote.ApiService
import com.example.e_commerce_app.data.toDomain
import com.example.e_commerce_app.data.toEntity
import com.example.e_commerce_app.domain.model.Category
import com.example.e_commerce_app.domain.model.Product
import com.example.e_commerce_app.domain.model.Review
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val api: ApiService,
    private val dao: ProductDao
) {

    fun observeProducts(): Flow<List<Product>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeByIds(ids: List<Int>): Flow<List<Product>> =
        dao.observeByIds(ids).map { list -> list.map { it.toDomain() } }

    suspend fun getProduct(id: Int): Product? {
        dao.byId(id)?.let { return it.toDomain() }
        return runCatching { api.getProduct(id).toDomain() }.getOrNull()
    }

    suspend fun getProductWithReviews(id: Int): Pair<Product, List<Review>>? {
        val dto = runCatching { api.getProduct(id) }.getOrElse {
            return dao.byId(id)?.let { it.toDomain() to emptyList() }
        }
        val reviews = dto.reviews?.map { it.toDomain() }.orEmpty()
        return dto.toDomain() to reviews
    }

    suspend fun refreshProducts(): Result<Unit> = runCatching {
        val response = api.getProducts(limit = 100)
        val now = System.currentTimeMillis()
        dao.upsertAll(response.products.map { it.toEntity(now) })
    }

    suspend fun ensureCached(): Result<Unit> {
        if (dao.count() > 0) return Result.success(Unit)
        return refreshProducts()
    }

    suspend fun getCategories(): List<Category> = runCatching {
        api.getCategories().map { it.toDomain() }
    }.getOrElse { emptyList() }
}