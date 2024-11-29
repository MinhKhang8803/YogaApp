package com.example.yoganativeapp.data

data class YogaClass(
    val id: String? = null,
    val dayOfWeek: String,
    val time: String,
    val capacity: Int,
    val duration: Int,
    val price: Double,
    val type: String,
    val description: String? = null
)
