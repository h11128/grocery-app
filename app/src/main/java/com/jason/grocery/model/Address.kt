package com.jason.grocery.model

import java.io.Serializable

data class Address(
    val __v: Int,
    val _id: String,
    val city: String,
    val houseNo: String,
    val pincode: Int,
    val streetName: String,
    val type: String,
    val userId: String
): Serializable{
    fun toShipAddress(): ShippingAddress{
        return ShippingAddress(_id, city, houseNo, pincode, type)
    }
}