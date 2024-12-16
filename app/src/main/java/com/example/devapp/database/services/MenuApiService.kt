package com.example.devapp.database.services

import com.example.devapp.database.models.Menu
import retrofit2.Response
import retrofit2.http.*

interface MenuApiService {
    @GET("menu")
    suspend fun getMenu(): Response<List<Menu>>

    @POST("menu")
    suspend fun addDish(@Body dish: Menu): Response<Menu>

    @GET("menu/{id}")
    suspend fun getDish(@Path("id") id: Int): Response<Menu>

    @DELETE("menu/{id}")
    suspend fun deleteDish(@Path("id") id: Int): Response<Unit>
}