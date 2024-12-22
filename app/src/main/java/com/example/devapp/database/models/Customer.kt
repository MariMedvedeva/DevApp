package com.example.devapp.database.models

import com.google.gson.annotations.SerializedName

data class Customer(
    val idclient: Int,
    @SerializedName("phone_number") val phoneNum: String,
    val mail: String?,
    val fullname: String
)
