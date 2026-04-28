package com.example.e_commerce_app.data.local

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromStringList(list: List<String>?): String =
        list?.joinToString(SEPARATOR) ?: ""

    @TypeConverter
    fun toStringList(value: String?): List<String> =
        if (value.isNullOrEmpty()) emptyList() else value.split(SEPARATOR)

    companion object {
        private const val SEPARATOR = ""
    }
}