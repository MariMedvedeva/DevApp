package com.example.devapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.devapp.databinding.ActivityAddDishBinding
import com.example.devapp.database.models.Menu
import com.example.devapp.views.MenuViewModel
import androidx.activity.viewModels

class AddDishActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddDishBinding
    private val menuViewModel: MenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSaveDish.setOnClickListener {
            // Получаем данные из полей ввода
            val dishName = binding.etDishName.text.toString()
            val dishPrice = binding.etDishPrice.text.toString().toDoubleOrNull()

            if (dishName.isEmpty() || dishPrice == null) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            } else {
                // Создаем объект Menu для нового блюда
                val newDish = Menu(
                    iddish = 0, // Мы используем 0, так как ID будет присвоено на сервере
                    namedish = dishName,
                    price = dishPrice
                )

                // Отправляем блюдо на сервер
                menuViewModel.addDish(newDish)

                // Сообщаем пользователю, что блюдо добавлено
                Toast.makeText(this, "Блюдо добавлено в меню", Toast.LENGTH_SHORT).show()

                // Закрываем активность и возвращаемся на предыдущую
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
