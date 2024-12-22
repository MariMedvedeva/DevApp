package com.example.devapp.database.api

import com.example.devapp.database.services.ApiService
import com.example.devapp.database.services.CustomerApiService
import com.example.devapp.database.services.MenuApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object ApiClient {
    //private const val BASE_URL = "http://10.0.2.2:3000/api/" // Для локального сервера
    private const val BASE_URL = "http://192.168.0.102:3000/api/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

    fun createCustomerService(): CustomerApiService {
        return createService(CustomerApiService::class.java)
    }
    // Создание экземпляра ApiService
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // Создание экземпляра MenuApiService
    val menuApiService: MenuApiService by lazy {
        retrofit.create(MenuApiService::class.java)
    }
}