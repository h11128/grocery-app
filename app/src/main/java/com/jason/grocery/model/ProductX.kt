package com.jason.grocery.model

data class ProductX(
    var _id: String,
    val image: String,
    val mrp: Int,
    val price: Int,
    val productName: String,
    val quantity: Int
)