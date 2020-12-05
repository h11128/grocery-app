package com.jason.grocery.model

import com.jason.grocery.data.Data3simple
import java.io.Serializable

data class Product(
    val count: Int,
    val `data`: List<Data3>,
    val error: Boolean
)

data class Data3(
    val __v: Int,
    val _id: String,
    val catId: Int,
    val created: String,
    val description: String,
    val image: String,
    val mrp: Any,
    val position: Int,
    val price: Double,
    val productName: String,
    var quantity: Int,
    val status: Boolean,
    val subId: Int,
    val unit: String
) : Serializable {
    fun getData3Simple(): Data3simple {
        val priceString = "$price#$mrp"
        return Data3simple(catId, image, productName, quantity = 1, priceString)
    }

    fun getData3SimpleSample(): Data3simple {
        val priceString = "$price#$mrp"
        return Data3simple(catId, image, productName, quantity = 0, priceString)
    }
}


