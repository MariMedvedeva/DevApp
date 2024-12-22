package com.example.devapp.views

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.devapp.database.models.Order
import com.example.devapp.database.models.OrderResponse
import com.example.devapp.database.services.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OrderViewModel : ViewModel() {

    private val _orders = MutableLiveData<List<OrderResponse>>()
    val orders: LiveData<List<OrderResponse>> get() = _orders

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.102:3000/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    fun fetchOrdersByUserId(userId: Int) {
        apiService.getOrdersByUserId(userId).enqueue(object : Callback<List<OrderResponse>> {
            override fun onResponse(call: Call<List<OrderResponse>>, response: Response<List<OrderResponse>>) {
                Log.d("API Request", "Request URL: ${call.request().url}")
                Log.d("API Request", "Request Body: ${call.request().body}")
                Log.d("API Response", "Response Body: ${response.body()}")
                if (response.isSuccessful) {
                    _orders.value = response.body()
                } else {
                    _errorMessage.value = "Ошибка при получении заказов ${response.errorBody()?.string()}"
                }
            }

            override fun onFailure(call: Call<List<OrderResponse>>, t: Throwable) {
                _errorMessage.value = "Ошибка сети"
            }
        })
    }

}