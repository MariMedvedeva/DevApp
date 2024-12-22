package com.example.devapp.views

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devapp.database.api.ApiClient
import com.example.devapp.database.models.Menu
import com.example.devapp.database.models.Order
import com.example.devapp.database.models.OrderItem
import com.example.devapp.database.services.MenuApiService
import kotlinx.coroutines.launch

class MenuViewModel : ViewModel() {
    val menuDishes = MutableLiveData<List<Menu>>()
    val orderItems = MutableLiveData<List<OrderItem>>()
    val order = MutableLiveData<Order>()
    val error = MutableLiveData<String>()

    fun fetchMenu() {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getMenu() // Это может быть ваш Retrofit вызов
                menuDishes.value = response // Мы передаем список в LiveData
            } catch (e: Exception) {
                error.value = "Ошибка: ${e.message}"
            }
        }
    }

    fun createOrder(order: Order) {
        viewModelScope.launch {
            try {
                Log.d("MenuViewModel", "Sending order: $order") // Log the order data
                val response = ApiClient.apiService.createOrder(order)
                Log.d("MenuViewModel", "Response: $response") // Log the response
                this@MenuViewModel.order.value = response
            } catch (e: Exception) {
                error.value = "Ошибка: ${e.message}"
                Log.e("MenuViewModel", "Error: ${e.message}", e)
            }
        }
    }

    fun addOrderItem(orderItem: OrderItem) {
        viewModelScope.launch {
            try {
                Log.d("MenuViewModel", "Adding order item: $orderItem") // Log order item data
                val response = ApiClient.apiService.addOrderItem(orderItem)
                Log.d("MenuViewModel", "Response: $response") // Log the response
                val currentItems = orderItems.value ?: emptyList()
                orderItems.value = currentItems + response
            } catch (e: Exception) {
                error.value = "Ошибка: ${e.message}"
                Log.e("MenuViewModel", "Error: ${e.message}", e)
            }
        }
    }
}
