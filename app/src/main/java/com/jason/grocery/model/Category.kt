package com.jason.grocery.model

import java.io.Serializable

data class Category(
    val count: Int,
    val `data`: List<Data1>,
    val error: Boolean
)

data class Data1(
    val __v: Int,
    val _id: String,
    val catDescription: String,
    val catId: Int,
    val catImage: String,
    val catName: String,
    val position: Int,
    val slug: String,
    val status: Boolean
) : Serializable