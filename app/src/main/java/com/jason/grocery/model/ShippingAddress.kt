package com.jason.grocery.model

data class ShippingAddress(
    val _id: String,
    val city: String,
    val houseNo: String,
    val pincode: Int,
    val type: String
)