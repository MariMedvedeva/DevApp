package com.example.devapp.database.models

data class OrderItem(
    //val idorder_item: Int = 0,
    val orderid: Int,
    val dishid: Int,
    val quantity: Int,
    val price: Double
)
