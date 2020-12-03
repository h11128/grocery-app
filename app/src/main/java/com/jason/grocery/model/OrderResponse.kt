package com.jason.grocery.model

data class OrderResponse(
    val `data`: Data,
    val error: Boolean,
    val message: String
)