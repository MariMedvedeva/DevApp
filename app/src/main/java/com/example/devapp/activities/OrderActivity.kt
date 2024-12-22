package com.example.devapp.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devapp.R
import com.example.devapp.adapters.OrderAdapter
import com.example.devapp.databinding.ActivityOrderBinding
import com.example.devapp.views.OrderViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.devapp.database.services.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding
    private val orderViewModel: OrderViewModel by viewModels()
    private lateinit var adapter: OrderAdapter
    private var userId: Int = -1
    private var isAdmin: Boolean = false

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.102:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем переданные данные (username и userId)
        val sharedPreferences = getSharedPreferences("USER_PREFS", MODE_PRIVATE)
        val username = sharedPreferences.getString("USER_NAME", "Гость") ?: "Гость"
        userId = sharedPreferences.getInt("USER_ID", -1)

        // Отображаем username и userId в TextView
        binding.tvUsername.text = "$username (ID: $userId)"

        if (userId != -1) {
            // Запрашиваем статус админа пользователя
            checkIfAdmin(userId)

            // Загружаем заказы для пользователя
            orderViewModel.fetchOrdersByUserId(userId)
        } else {
            Toast.makeText(this, "Пользователь не найден", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Инициализируем RecyclerView и создаем адаптер
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = OrderAdapter(emptyList(), { orderId ->
            // Обработчик для изменения статуса
            changeOrderStatus(orderId)
        }, { orderId ->
            // Обработчик для отмены заказа
            cancelOrder(orderId)
        }, isAdmin)  // Передаем информацию о том, админ ли пользователь

        binding.recyclerView.adapter = adapter

        // Наблюдаем за изменениями в списке заказов
        orderViewModel.orders.observe(this) { orders ->
            adapter = OrderAdapter(orders, { orderId ->
                changeOrderStatus(orderId)
            }, { orderId ->
                cancelOrder(orderId)
            }, isAdmin)  // Передаем флаг в адаптер
            binding.recyclerView.adapter = adapter
        }

        // Наблюдаем за ошибками
        orderViewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    // Запрос на проверку, является ли пользователь администратором
    private fun checkIfAdmin(userId: Int) {
        apiService.isAdmin(userId).enqueue(object : Callback<Map<String, Boolean>> {
            override fun onResponse(
                call: Call<Map<String, Boolean>>,
                response: Response<Map<String, Boolean>>
            ) {
                if (response.isSuccessful) {
                    // Проверяем ответ API
                    isAdmin = response.body()?.get("isAdmin") ?: false

                    // Сохраняем флаг админа в SharedPreferences
                    val sharedPreferences = getSharedPreferences("USER_PREFS", MODE_PRIVATE)
                    sharedPreferences.edit().putBoolean("IS_ADMIN", isAdmin).apply()

                    // Обновляем адаптер с информацией о том, что пользователь админ
                    adapter = OrderAdapter(
                        orders = orderViewModel.orders.value ?: emptyList(),
                        onChangeStatus = { orderId -> changeOrderStatus(orderId) },
                        onCancelOrder = { orderId -> cancelOrder(orderId) },
                        isAdmin = isAdmin // Передаем флаг в адаптер
                    )
                    binding.recyclerView.adapter = adapter

                    // Обновляем интерфейс в зависимости от того, админ ли пользователь
                    if (isAdmin) {
                        binding.tvUsername.text = "${binding.tvUsername.text} (Admin)"
                        hideAdminButtons()
                    } else {
                        binding.tvUsername.text = "${binding.tvUsername.text} (User)"
                    }
                } else {
                    Toast.makeText(this@OrderActivity, "Ошибка при получении статуса админа", Toast.LENGTH_SHORT).show()
                    Log.e("OrderActivity", "Error checking admin status: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Map<String, Boolean>>, t: Throwable) {
                Toast.makeText(this@OrderActivity, "Ошибка запроса на проверку роли", Toast.LENGTH_SHORT).show()
                Log.e("OrderActivity", "Error checking admin status: ${t.message}")
            }
        })
    }

    // Функция для изменения статуса заказа
    private fun changeOrderStatus(orderId: Int) {
        apiService.changeOrderStatus(orderId).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    // Обновляем список заказов после изменения статуса
                    orderViewModel.fetchOrdersByUserId(userId)
                    Toast.makeText(this@OrderActivity, "Статус заказа изменен", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@OrderActivity, "Ошибка изменения статуса", Toast.LENGTH_SHORT).show()
                    Log.e("OrderActivity", "Error canceling order: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Toast.makeText(this@OrderActivity, "Ошибка при запросе на изменение статуса", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Функция для отмены заказа
    private fun cancelOrder(orderId: Int) {
        Log.d("OrderActivity", "Attempting to cancel order with ID: $orderId")
        apiService.cancelOrder(orderId).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    // Обновляем список заказов после отмены
                    orderViewModel.fetchOrdersByUserId(userId)
                    Toast.makeText(this@OrderActivity, "Заказ отменен", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("OrderActivity", "Error canceling order. HTTP code: ${response.code()}")
                    Log.e("OrderActivity", "Error body: ${response.errorBody()?.string()}")
                    Toast.makeText(this@OrderActivity, "Ошибка отмены заказа", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Log.e("OrderActivity", "Failure to cancel order: ${t.message}")
                Toast.makeText(this@OrderActivity, "Ошибка при запросе на отмену заказа", Toast.LENGTH_SHORT).show()
            }
        })
    }


    // Функция для скрытия кнопок изменения статуса и отмены заказа
    private fun hideAdminButtons() {
        // Прячем кнопки изменения статуса и отмены заказа, если пользователь не админ
        binding.recyclerView.findViewById<Button>(R.id.btnChangeStatus)?.visibility = View.GONE
        binding.recyclerView.findViewById<Button>(R.id.btnCancelOrder)?.visibility = View.GONE
    }
}

