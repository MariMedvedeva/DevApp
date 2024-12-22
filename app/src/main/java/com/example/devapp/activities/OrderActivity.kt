package com.example.devapp.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devapp.R
import com.example.devapp.adapters.OrderAdapter
import com.example.devapp.databinding.ActivityOrderBinding
import com.example.devapp.views.OrderViewModel
import androidx.recyclerview.widget.RecyclerView

class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding
    private val orderViewModel: OrderViewModel by viewModels()
    private lateinit var adapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем переданные данные (username и userId)
        val username = intent.getStringExtra("USER_NAME") ?: "Гость"
        val userId = intent.getIntExtra("USER_ID", -1)

        // Отображаем username и userId в TextView
        binding.tvUsername.text = "$username (ID: $userId)"

        if (userId != -1) {
            // Загружаем заказы для пользователя
            orderViewModel.fetchOrdersByUserId(userId)
        } else {
            Toast.makeText(this, "Пользователь не найден", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Инициализируем RecyclerView и создаем адаптер
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = OrderAdapter(emptyList()) // Инициализация с пустым списком
        binding.recyclerView.adapter = adapter

        // Наблюдаем за изменениями в списке заказов
        orderViewModel.orders.observe(this) { orders ->
            val adapter = OrderAdapter(orders)
            binding.recyclerView.adapter = adapter // Устанавливаем адаптер
        }

        // Наблюдаем за ошибками
        orderViewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
}
