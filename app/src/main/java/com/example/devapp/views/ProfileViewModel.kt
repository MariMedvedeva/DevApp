package com.example.devapp.views

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devapp.database.api.ApiClient
import com.example.devapp.database.models.Customer
import com.example.devapp.database.services.CustomerApiService
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val apiService: CustomerApiService = ApiClient.createCustomerService()
    val customer = MutableLiveData<Customer?>()
    val error = MutableLiveData<String>()

    fun fetchCustomer(userId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.getCustomerByUserId(userId)
                if (response.isSuccessful) {
                    customer.value = response.body()
                } else {
                    error.value = "Ошибка сервера: ${response.code()}"
                }
            } catch (e: Exception) {
                error.value = "Ошибка: ${e.message}"
            }
        }
    }

    fun saveOrUpdateCustomer(userId: Int, updatedCustomer: Customer) {
        viewModelScope.launch {
            try {
                val existingCustomerResponse = apiService.getCustomerByUserId(userId)
                if (existingCustomerResponse.isSuccessful) {
                    val existingCustomer = existingCustomerResponse.body()
                    if (existingCustomer != null) {
                        // Если клиент существует, обновляем его
                        val updateResponse = apiService.updateCustomer(userId, updatedCustomer)
                        if (updateResponse.isSuccessful) {
                            customer.value = updateResponse.body()
                        } else {
                            error.value = "Ошибка при обновлении данных"
                        }
                    }
                }
            } catch (e: Exception) {
                error.value = "Ошибка при сохранении данных: ${e.message}"
            }
        }
    }
}