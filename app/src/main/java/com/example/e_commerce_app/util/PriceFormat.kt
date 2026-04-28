package com.example.e_commerce_app.util

import java.text.NumberFormat
import java.util.Locale

object PriceFormat {
    private val formatter: NumberFormat = NumberFormat.getNumberInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 0
    }

    fun format(amount: Double): String = "₹" + formatter.format(amount)
}