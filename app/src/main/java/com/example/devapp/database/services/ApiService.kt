package com.example.devapp.database.services

import com.example.devapp.database.models.Menu
import com.example.devapp.database.models.Order
import com.example.devapp.database.models.OrderItem
import com.example.devapp.database.models.OrderResponse
import com.example.devapp.database.models.UserIdResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {
    @GET("api/user/getUserId")
    fun getUserId(@Query("username") username: String): Call<UserIdResponse>

    @GET("api/user/isAdmin/{userId}")
    fun isAdmin(@Path("userId") userId: Int): Call<Map<String, Boolean>>

    @GET("user/isAdmin/{userId}")
    suspend fun isAdmins(@Path("userId") userId: Int): Boolean

    // Получить меню
    @GET("menu")
    suspend fun getMenu(): List<Menu>

    // Добавить блюдо в меню
    @POST("menu")
    suspend fun addDish(@Body dish: Menu): Menu

    @DELETE("menu/{id}")
    suspend fun deleteDish(@Path("id") dishId: Int): Response<Void>

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
    fun getOrdersByUserId(@Path("userId") userId: Int): Call<List<OrderResponse>>

    // Обновление статуса заказа
    @PUT("api/orders/{orderId}/status")
    fun changeOrderStatus(@Path("orderId") orderId: Int): Call<Map<String, Any>>

    // Отмена заказа
    @PUT("api/orders/{orderId}/cancel")
    fun cancelOrder(@Path("orderId") orderId: Int): Call<Map<String, Any>>
}