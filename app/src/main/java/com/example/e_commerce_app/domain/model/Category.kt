package com.example.e_commerce_app.domain.model

data class Category(
    val slug: String,
    val name: String
) {
    companion object {
        val ALL = Category(slug = "", name = "All")
    }
}