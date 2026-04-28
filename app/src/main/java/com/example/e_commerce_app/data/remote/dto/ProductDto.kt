package com.example.e_commerce_app.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductsResponse(
    @Json(name = "products") val products: List<ProductDto>,
    @Json(name = "total") val total: Int = 0,
    @Json(name = "skip") val skip: Int = 0,
    @Json(name = "limit") val limit: Int = 0
)

@JsonClass(generateAdapter = true)
data class ProductDto(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String? = null,
    @Json(name = "category") val category: String? = null,
    @Json(name = "price") val price: Double,
    @Json(name = "discountPercentage") val discountPercentage: Double? = null,
    @Json(name = "rating") val rating: Double? = null,
    @Json(name = "stock") val stock: Int? = null,
    @Json(name = "brand") val brand: String? = null,
    @Json(name = "thumbnail") val thumbnail: String? = null,
    @Json(name = "images") val images: List<String>? = null,
    @Json(name = "tags") val tags: List<String>? = null,
    @Json(name = "reviews") val reviews: List<ReviewDto>? = null
)

@JsonClass(generateAdapter = true)
data class ReviewDto(
    @Json(name = "rating") val rating: Int,
    @Json(name = "comment") val comment: String,
    @Json(name = "date") val date: String? = null,
    @Json(name = "reviewerName") val reviewerName: String,
    @Json(name = "reviewerEmail") val reviewerEmail: String? = null
)

@JsonClass(generateAdapter = true)
data class CategoryDto(
    @Json(name = "slug") val slug: String,
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String? = null
)