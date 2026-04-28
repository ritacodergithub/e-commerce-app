package com.example.e_commerce_app.data

import com.example.e_commerce_app.data.local.entity.OrderEntity
import com.example.e_commerce_app.data.local.entity.OrderLineEntity
import com.example.e_commerce_app.data.local.entity.ProductEntity
import com.example.e_commerce_app.data.remote.dto.CategoryDto
import com.example.e_commerce_app.data.remote.dto.ProductDto
import com.example.e_commerce_app.data.remote.dto.ReviewDto
import com.example.e_commerce_app.domain.model.Category
import com.example.e_commerce_app.domain.model.Order
import com.example.e_commerce_app.domain.model.OrderLine
import com.example.e_commerce_app.domain.model.Product
import com.example.e_commerce_app.domain.model.Review

fun ProductDto.toEntity(now: Long): ProductEntity = ProductEntity(
    id = id,
    title = title,
    description = description.orEmpty(),
    category = category.orEmpty(),
    price = price,
    discountPercentage = discountPercentage ?: 0.0,
    rating = rating ?: 0.0,
    stock = stock ?: 0,
    brand = brand.orEmpty(),
    thumbnail = thumbnail.orEmpty(),
    images = images.orEmpty(),
    tags = tags.orEmpty(),
    updatedAt = now
)

fun ProductEntity.toDomain(): Product = Product(
    id = id,
    title = title,
    description = description,
    category = category,
    price = price,
    discountPercentage = discountPercentage,
    rating = rating,
    stock = stock,
    brand = brand,
    thumbnail = thumbnail,
    images = images,
    tags = tags
)

fun ProductDto.toDomain(): Product = Product(
    id = id,
    title = title,
    description = description.orEmpty(),
    category = category.orEmpty(),
    price = price,
    discountPercentage = discountPercentage ?: 0.0,
    rating = rating ?: 0.0,
    stock = stock ?: 0,
    brand = brand.orEmpty(),
    thumbnail = thumbnail.orEmpty(),
    images = images.orEmpty(),
    tags = tags.orEmpty()
)

fun CategoryDto.toDomain(): Category = Category(slug = slug, name = name)

fun ReviewDto.toDomain(): Review = Review(
    rating = rating,
    comment = comment,
    reviewer = reviewerName,
    date = date
)

fun OrderEntity.toDomain(lines: List<OrderLineEntity>): Order = Order(
    orderId = orderId,
    placedAt = placedAt,
    subtotal = subtotal,
    shipping = shipping,
    tax = tax,
    total = total,
    paymentMethod = paymentMethod,
    shippingAddress = shippingAddress,
    itemCount = itemCount,
    lines = lines.map { it.toDomain() }
)

fun OrderLineEntity.toDomain(): OrderLine = OrderLine(
    productId = productId,
    title = title,
    brand = brand,
    thumbnail = thumbnail,
    price = price,
    quantity = quantity
)