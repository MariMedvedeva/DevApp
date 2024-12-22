package com.example.devapp.database.services

import com.example.devapp.database.models.Menu
import com.example.devapp.database.models.Order
import com.example.devapp.database.models.OrderItem
import com.example.devapp.database.models.UserIdResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {
    @GET("api/user/getUserId")
    fun getUserId(@Query("username") username: String): Call<UserIdResponse>

    // Получить меню
    @GET("menu")
    suspend fun getMenu(): List<Menu>

    // Добавить блюдо в меню
    @POST("menu")
    suspend fun addDish(@Body dish: Menu): Menu

    // Получить заказ
    @GET("api/orders/{id}")
    suspend fun getOrder(@Path("id") orderId: Int): Order

    // Создать заказ
    @POST("orders/create")
    suspend fun createOrder(@Body order: Order): Order

    // Добавить позицию в заказ
    @POST("orders/order_items")
    suspend fun addOrderItem(@Body orderItem: OrderItem): OrderItem

    @GET("orders/{userId}")
    fun getOrdersByUserId(@Path("userId") userId: Int): Call<List<Order>>
}