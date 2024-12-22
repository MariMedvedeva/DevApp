package com.example.devapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devapp.adapters.MenuAdapter
import com.example.devapp.database.models.Menu
import com.example.devapp.database.models.Order
import com.example.devapp.database.models.OrderItem
import com.example.devapp.databinding.ActivityMenuBinding
import com.example.devapp.views.MenuViewModel
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private val menuViewModel: MenuViewModel by viewModels()
    private lateinit var menuAdapter: MenuAdapter
    private val selectedItems = mutableListOf<OrderItem>() // Для хранения выбранных позиций меню

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем данные из Intent
        val username = intent.getStringExtra("USER_NAME") ?: "Guest"
        val userId = intent.getIntExtra("USER_ID", -1)

        // Отображаем имя пользователя и ID
        binding.tvUsername.text = "$username (ID: $userId)"

        // Настройка RecyclerView для отображения меню
        menuAdapter = MenuAdapter(emptyList()) { dish, quantity ->
            // Проверяем, если такой товар уже есть в списке
            val existingItem = selectedItems.find { it.dishid == dish.iddish }

            if (existingItem != null) {
                // Если товар уже в списке, обновляем его количество
                val updatedItem = existingItem.copy(quantity = quantity)
                selectedItems[selectedItems.indexOf(existingItem)] = updatedItem
            } else {
                // Если товара нет в списке, добавляем его
                selectedItems.add(
                    OrderItem(
                        orderid = 1, // ID заказа
                        dishid = dish.iddish,
                        quantity = quantity,
                        price = dish.price
                    )
                )
            }
            // Обновляем общую сумму
            updateTotalPrice()
        }
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
            // Фильтруем товары, у которых количество больше 0
            val filteredItems = selectedItems.filter { it.quantity > 0 }

            if (filteredItems.isEmpty()) {
                // Если после фильтрации список пуст, возвращаем ошибку или показываем сообщение
                Toast.makeText(this, "Добавьте хотя бы один товар в заказ", Toast.LENGTH_SHORT).show()
            } else {
                // Создаем заказ, если есть хотя бы один товар
                val newOrder = Order(
                    clientid = userId, // ID клиента, полученный через Intent
                    statusid = 1, // Статус всегда 1
                    orderdate = ZonedDateTime.now(ZoneId.of("Europe/Moscow")).format(
                        DateTimeFormatter.ISO_LOCAL_DATE),
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

        binding.btnAddOrderItem.setOnClickListener {
            // Фильтруем товары, у которых количество больше 0
            val validItems = selectedItems.filter { it.quantity > 0 }

            validItems.forEach { item ->
                // Добавляем товар только если его количество больше 0
                menuViewModel.addOrderItem(item)
            }
        }
    }
    // Функция для обновления общей суммы
    private fun updateTotalPrice() {
        val totalPrice = selectedItems.sumOf { it.price * it.quantity }
        binding.tvTotalPrice.text = "Общая сумма: ${totalPrice} ₽"
    }

}
