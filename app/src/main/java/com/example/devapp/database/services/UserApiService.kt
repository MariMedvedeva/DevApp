package com.example.devapp.database.services

import com.example.devapp.database.models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {
    @POST("auth/register")
    fun register(@Body user: User): Call<Void>

    @POST("auth/login")
    fun login(@Body user: User): Call<User>
}