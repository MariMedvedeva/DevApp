package com.example.devapp.database.models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Order(
    //val idorder: Int? = 0,
    val clientid: Int,
    val statusid: Int = 1,
    val orderdate: String? = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
    val price: Double,
    val items: List<OrderItem>?
)
