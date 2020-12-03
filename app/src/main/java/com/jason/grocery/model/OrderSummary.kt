package com.jason.grocery.model

import java.io.Serializable

data class OrderSummary(
    var _id: String,
    val deliveryCharges: Int,
    val discount: Double,
    val orderAmount: Int,
    val ourPrice: Double,
    val totalAmount: Int
):Serializable