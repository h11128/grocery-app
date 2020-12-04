package com.jason.grocery.model

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class Order(
    val __v: Int,
    val _id: String,
    val date: String,
    val orderStatus: String,
    val orderSummary: OrderSummary,
    val products: List<ProductX>,
    val shippingAddress: ShippingAddress,
    val user: User,
    val userId: String
) {
    fun showString(): String {
        var details = " "
        details += "date: ${date}\n\n"
        details += "orderStatus: ${orderStatus}\n\n"
        details += "orderSummary: ${orderSummary}\n\n"
        details += "products: ${products}\n\n"
        details += "shippingAddress: ${shippingAddress}\n\n"
        details += "user: ${user}\n\n"
        details += "userId: ${userId}\n\n"

        return details
    }

    fun toSendOrder(): SendOrder {
        return SendOrder(orderStatus, orderSummary, products, shippingAddress, user, userId)
    }

}

data class SendOrder(
    val orderStatus: String,
    val orderSummary: OrderSummary,
    val products: List<ProductX>,
    val shippingAddress: ShippingAddress,
    val user: User,
    val userId: String
) {
    interface SendOrderApi {
        companion object {
            operator fun invoke(): SendOrderApi {
                return Retrofit.Builder()
                    .baseUrl(url_api)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(SendOrderApi::class.java)
            }
        }

        @POST("orders")
        fun postOrder(@Body sendOrder: SendOrder): Call<OrderResponse>

    }

}