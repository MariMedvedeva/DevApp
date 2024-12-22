package com.example.devapp.database.models

import java.time.LocalDateTime

data class Delivery(
    val iddelivery: Int,
    val orderid: Int,
    val courierid: Int,
    val deliveryTime: LocalDateTime, // ,String?
    val statusid: Int?
)
