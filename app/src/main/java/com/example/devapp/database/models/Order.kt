package com.example.devapp.database.models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Order(
    //val idorder: Int?,
    val clientid: Int,
    val statusid: Int = 1,
    val orderdate: String?,
    val price: Double,
    val items: List<OrderItem>?
)
