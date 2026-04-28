package com.example.e_commerce_app.domain.model

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val brand: String,
    val thumbnail: String,
    val images: List<String>,
    val tags: List<String>
) {
    val originalPrice: Double
        get() = if (discountPercentage > 0) price / (1.0 - discountPercentage / 100.0) else price

    val discountPercentInt: Int
        get() = discountPercentage.toInt()

    val displayCategory: String
        get() = category.split('-').joinToString(" ") {
            it.replaceFirstChar { c -> c.uppercase() }
        }
}