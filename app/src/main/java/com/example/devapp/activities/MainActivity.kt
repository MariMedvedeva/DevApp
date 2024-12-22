package com.example.devapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devapp.R
import com.example.devapp.adapters.MenuAdapter
import com.example.devapp.database.models.OrderItem
import com.example.devapp.database.models.UserIdResponse
import com.example.devapp.database.services.ApiService
import com.example.devapp.databinding.ActivityMainBinding
import com.example.devapp.views.MenuViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MenuViewModel by viewModels()
    private val selectedItems = mutableListOf<OrderItem>() // Для хранения выбранных позиций меню


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("USER_PREFS", MODE_PRIVATE)
        val userToken = sharedPreferences.getString("USER_TOKEN", null)
        val username = sharedPreferences.getString("USER_NAME", "Guest") // Имя пользователя

        if (userToken == null) {
            // Если токен пользователя отсутствует, открываем форму для входа
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            // Иначе, отображаем меню
            val recyclerView = binding.recyclerViewMenu
            recyclerView.layoutManager = LinearLayoutManager(this)

            // Получаем меню и отображаем его
            viewModel.menuDishes.observe(this) { dishes ->
                recyclerView.adapter = MenuAdapter(dishes) { selectedDish, quantity ->
                    // Логика для добавления или уменьшения количества блюда в заказе
                    val existingItem = selectedItems.find { it.dishid == selectedDish.iddish }
                    if (existingItem != null) {
                        // Если элемент найден, обновляем его количество
                        val updatedItem = existingItem.copy(quantity = quantity)
                        selectedItems[selectedItems.indexOf(existingItem)] = updatedItem
                    } else {
                        // Если блюда нет в списке, добавляем новый элемент
                        selectedItems.add(
                            OrderItem(
                                orderid = 1, // Здесь может быть ID заказа, если он динамически меняется
                                dishid = selectedDish.iddish,
                                quantity = quantity,
                                price = selectedDish.price
                            )
                        )
                    }
                }
            }

            viewModel.error.observe(this) { errorMessage ->
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
            }

            viewModel.fetchMenu()

            // Запрашиваем userId по username через Retrofit
            if (username != null) {
                fetchUserId(username)
            }

            // Logout Button
            binding.logoutButton.setOnClickListener {
                sharedPreferences.edit().remove("USER_TOKEN").apply()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

            binding.menuButton.setOnClickListener {
                //startActivity(Intent(this, MenuActivity::class.java))
                val username = sharedPreferences.getString("USER_NAME", "Guest")
                val userId = sharedPreferences.getInt("USER_ID", -1)

                val intent = Intent(this, MenuActivity::class.java).apply {
                    putExtra("USER_NAME", username)
                    putExtra("USER_ID", userId)
                }
                startActivity(intent)
            }

            binding.orderButton.setOnClickListener {
                //val intent = Intent(this, OrderActivity::class.java)
                val username = sharedPreferences.getString("USER_NAME", "Guest")
                val userId = sharedPreferences.getInt("USER_ID", -1)

                val intent = Intent(this, OrderActivity::class.java).apply {
                    putExtra("USER_NAME", username)
                    putExtra("USER_ID", userId)
                }
                Log.d("MainActivity", "Открывается OrderActivity с USER_ID: $userId и USER_NAME: $username")
                startActivity(intent)

            }

            binding.profileButton.setOnClickListener {
                val userId = sharedPreferences.getInt("USER_ID", -1)
                Log.d("MainActivity", "USER_ID: $userId")

                if (userId != -1) {
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                } else {
                    Snackbar.make(binding.root, "Пользователь не авторизован", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchUserId(username: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.102:3000") // Замените на URL вашего сервера
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.getUserId(username).enqueue(object : Callback<UserIdResponse> {
            override fun onResponse(call: Call<UserIdResponse>, response: Response<UserIdResponse>) {
                if (response.isSuccessful) {
                    val userId = response.body()?.userId
                    Log.d("MainActivity", "User ID from server: $userId")

                    if (userId != null) {
                        // Сохраняем userId в SharedPreferences
                        val sharedPreferences = getSharedPreferences("USER_PREFS", MODE_PRIVATE)
                        sharedPreferences.edit().putInt("USER_ID", userId).apply()

                        // Обновляем UI
                        binding.tvUsername.text = "$username (ID: $userId)"
                    } else {
                        // Ошибка с пустым userId
                        binding.tvUsername.text = "$username (ID: неизвестно)"
                        Log.e("MainActivity", "User ID is null")
                    }
                } else {
                    // Ошибка в ответе
                    val errorMessage = response.errorBody()?.string() ?: "Неизвестная ошибка"
                    Log.e("MainActivity", "Error fetching user ID: $errorMessage")
                    binding.tvUsername.text = "$username (ID: неизвестно)"
                }
            }

            override fun onFailure(call: Call<UserIdResponse>, t: Throwable) {
                // Обработка ошибок сети
                Log.e("MainActivity", "Error fetching user ID", t)
                binding.tvUsername.text = "$username (ID: неизвестно)"
            }
        })
    }
}