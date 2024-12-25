package com.example.devapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devapp.adapters.MenuAdapter
import com.example.devapp.database.models.Menu
import com.example.devapp.database.models.Order
import com.example.devapp.database.models.OrderItem
import com.example.devapp.database.services.ApiService
import com.example.devapp.databinding.ActivityMenuBinding
import com.example.devapp.views.MenuViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private val menuViewModel: MenuViewModel by viewModels()
    private lateinit var menuAdapter: MenuAdapter
    private val selectedItems = mutableListOf<OrderItem>()
    private var isAdmin = false  // Флаг, который будет хранить информацию о роли пользователя

    // Инициализация Retrofit и API сервиса
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.103:3000/")  // Укажите свой базовый URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем данные из Intent
        val username = intent.getStringExtra("USER_NAME") ?: "Guest"
        val userId = intent.getIntExtra("USER_ID", -1)

        // Отображаем имя пользователя и ID
        binding.tvUsername.text = "$username"

        // Проверяем, является ли пользователь администратором
        if (userId != -1) {
            checkIfAdmin(userId)
        } else {
            Toast.makeText(this, "Пользователь не найден", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Настройка RecyclerView для отображения меню
        menuAdapter = MenuAdapter(emptyList(), { dish, quantity ->
            // Логика добавления позиции в заказ
            val existingItem = selectedItems.find { it.dishid == dish.iddish }
            if (existingItem != null) {
                val updatedItem = existingItem.copy(quantity = quantity)
                selectedItems[selectedItems.indexOf(existingItem)] = updatedItem
            } else {
                selectedItems.add(
                    OrderItem(
                        orderid = 1,
                        dishid = dish.iddish,
                        quantity = quantity,
                        price = dish.price
                    )
                )
            }
            updateTotalPrice()
        }, isAdmin, menuViewModel)  // Передаем флаг, указывающий, администратор ли пользователь

        binding.rvMenu.layoutManager = LinearLayoutManager(this)
        binding.rvMenu.adapter = menuAdapter

        // Настроим ViewModel
        menuViewModel.menuDishes.observe(this, Observer { dishes ->
            dishes?.let {
                menuAdapter.setMenuList(it)
            }
        })

        // Получаем меню
        menuViewModel.fetchMenu()

        // Обработка кнопки "Оформить заказ"
        binding.btnCreateOrder.setOnClickListener {
            val filteredItems = selectedItems.filter { it.quantity > 0 }
            if (filteredItems.isEmpty()) {
                Toast.makeText(this, "Добавьте хотя бы один товар в заказ", Toast.LENGTH_SHORT).show()
            } else {
                val newOrder = Order(
                    clientid = userId,
                    statusid = 1,
                    orderdate = ZonedDateTime.now(ZoneId.of("Europe/Moscow")).format(DateTimeFormatter.ISO_LOCAL_DATE),
                    price = filteredItems.sumOf { it.price * it.quantity },
                    items = filteredItems
                )
                menuViewModel.createOrder(newOrder)

                // Показываем сообщение о том, что заказ оформлен
                Toast.makeText(this, "Заказ оформлен", Toast.LENGTH_SHORT).show()

                // Возвращаемся на MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }


        // Кнопка добавления нового блюда (видима только для администратора)
        binding.btnAddNewDish.setOnClickListener {
            val intent = Intent(this, AddDishActivity::class.java)
            startActivity(intent)
            menuViewModel.fetchMenu()
        }

    }

    // Проверка, является ли пользователь администратором
    private fun checkIfAdmin(userId: Int) {
        apiService.isAdmin(userId).enqueue(object : Callback<Map<String, Boolean>> {
            override fun onResponse(
                call: Call<Map<String, Boolean>>,
                response: Response<Map<String, Boolean>>
            ) {
                if (response.isSuccessful) {
                    // Получаем статус администратора
                    isAdmin = response.body()?.get("isAdmin") ?: false

                    // Обновляем UI в зависимости от того, админ ли пользователь
                    runOnUiThread {
                        if (isAdmin) {
                            binding.btnAddNewDish.visibility = View.VISIBLE
                        } else {
                            binding.btnAddNewDish.visibility = View.GONE
                        }
                    }
                } else {
                    Log.e("MenuActivity", "Error checking admin status: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Map<String, Boolean>>, t: Throwable) {
                Log.e("MenuActivity", "Error checking admin status: ${t.message}")
            }
        })
    }

    private fun updateTotalPrice() {
        val totalPrice = selectedItems.sumOf { it.price * it.quantity }
        binding.tvTotalPrice.text = "Общая сумма: ${totalPrice} ₽"
    }
}




