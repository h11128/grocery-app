package com.jason.grocery.model

data class SubCategory(
    val count: Int,
    val `data`: List<Data2>,
    val error: Boolean
)

data class Data2(
    val __v: Int,
    val _id: String,
    val catId: Int,
    val position: Int,
    val status: Boolean,
    val subDescription: String,
    val subId: Int,
    val subImage: String,
    val subName: String
)