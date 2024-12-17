package com.example.devapp.database.models

data class User(
    val id: Int,
    val username: String,
    val password: String,
    val token: String? = null
)
