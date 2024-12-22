package com.example.devapp.database.models

import java.time.LocalDateTime

data class Payment(
    val idpayment: Int,
    val orderid: Int,
    val paymentDate: LocalDateTime, // ,String?
    val amount: Double?,
    val paymentMethodId: Int?
)
