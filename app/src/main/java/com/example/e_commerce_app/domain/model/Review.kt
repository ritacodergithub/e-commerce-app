package com.example.e_commerce_app.domain.model

data class Review(
    val rating: Int,
    val comment: String,
    val reviewer: String,
    val date: String?
)