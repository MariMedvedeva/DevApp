package com.example.devapp.views

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devapp.database.api.ApiClient
import com.example.devapp.database.models.Menu
import com.example.devapp.database.services.MenuApiService
import kotlinx.coroutines.launch

class MenuViewModel : ViewModel() {
    private val apiService = ApiClient.createService(MenuApiService::class.java)
    val menuDishes = MutableLiveData<List<Menu>>()
    val error = MutableLiveData<String>()

    init {
        fetchMenu()
    }

    fun fetchMenu() {
        viewModelScope.launch {
            try {
                val response = apiService.getMenu()
                if (response.isSuccessful) {
                    menuDishes.value = response.body()
                } else {
                    error.value = "Ошибка сервера: ${response.code()}"
                }
            } catch (e: Exception) {
                error.value = "Ошибка: ${e.message}"
            }
        }
    }
}