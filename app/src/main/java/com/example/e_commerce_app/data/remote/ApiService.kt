package com.example.e_commerce_app.data.remote

import com.example.e_commerce_app.data.remote.dto.CategoryDto
import com.example.e_commerce_app.data.remote.dto.ProductDto
import com.example.e_commerce_app.data.remote.dto.ProductsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int = 100,
        @Query("skip") skip: Int = 0
    ): ProductsResponse

    @GET("products/categories")
    suspend fun getCategories(): List<CategoryDto>

    @GET("products/category/{slug}")
    suspend fun getProductsByCategory(
        @Path("slug") slug: String
    ): ProductsResponse

    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("limit") limit: Int = 50
    ): ProductsResponse

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Int): ProductDto

    companion object {
        const val BASE_URL = "https://dummyjson.com/"
    }
}