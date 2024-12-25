package com.example.devapp.views

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devapp.database.api.ApiClient
import com.example.devapp.database.models.Menu
import com.example.devapp.database.models.Order
import com.example.devapp.database.models.OrderItem
import com.example.devapp.database.services.ApiService
import com.example.devapp.database.services.MenuApiService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MenuViewModel : ViewModel() {
    val menuDishes = MutableLiveData<List<Menu>>()
    val error = MutableLiveData<String>()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.103:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    suspend fun isAdmin(userId: Int): Boolean {
        return try {
            val response = apiService.isAdmins(userId)
            Log.d("MenuViewModel", "Admin check response: $response")
            response
        } catch (e: Exception) {
            Log.e("MenuViewModel", "Error checking admin status: ${e.message}")
            false
        }
    }

    fun fetchMenu() {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getMenu()
                menuDishes.value = response
            } catch (e: Exception) {
                error.value = "Ошибка: ${e.message}"
            }
        }
    }

    fun createOrder(order: Order) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.createOrder(order)
            } catch (e: Exception) {
                error.value = "Ошибка: ${e.message}"
            }
        }
    }

    fun addDish(dish: Menu) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.addDish(dish)
                fetchMenu()
            } catch (e: Exception) {
                error.value = "Ошибка при добавлении блюда: ${e.message}"
                Log.e("MenuViewModel", "Error: ${e.message}", e)
            }
        }
    }
    fun deleteDish(dishId: Int) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.deleteDish(dishId)
                if (response.isSuccessful) {
                    fetchMenu()
                } else {
                    error.value = "Ошибка при удалении блюда"
                }
            } catch (e: Exception) {
                error.value = "Ошибка: ${e.message}"
                Log.e("MenuViewModel", "Error: ${e.message}", e)
            }
        }
    }
}



