package com.jason.grocery.model

data class Data(
    val __v: Int,
    val _id: String,
    val date: String,
    val orderStatus: String,
    val orderSummary: OrderSummaryX,
    val products: List<ProductXX>,
    val shippingAddress: ShippingAddressX,
    val user: UserX,
    val userId: String
)