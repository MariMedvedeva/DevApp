package com.example.devapp.database.services

import com.example.devapp.database.models.Customer
import okhttp3.*
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.io.IOException

interface CustomerApiService {
    @GET("customer/{id}")
    suspend fun getCustomerByUserId(@Path("id") id: Int): Response<Customer>

    @POST("customer/{id}")
    suspend fun addCustomer(@Path("id") id: Int, @Body customer: Customer): Response<Customer>

    @PUT("customer/{id}")
    suspend fun updateCustomer(@Path("id") id: Int, @Body customer: Customer): Response<Customer>
}