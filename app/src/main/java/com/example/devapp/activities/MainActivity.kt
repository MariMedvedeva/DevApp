package com.example.devapp.activities

import android.content.Intent
import android.os.Bundle
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
import com.example.devapp.databinding.ActivityMainBinding
import com.example.devapp.views.MenuViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("USER_PREFS", MODE_PRIVATE)
        val userToken = sharedPreferences.getString("USER_TOKEN", null)

        if (userToken == null) {
            // Если токен пользователя отсутствует, открываем форму для входа
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            // Иначе, отображаем меню
            val recyclerView = binding.recyclerViewMenu
            recyclerView.layoutManager = LinearLayoutManager(this)

            viewModel.menuDishes.observe(this) { dishes ->
                recyclerView.adapter = MenuAdapter(dishes)
            }

            viewModel.error.observe(this) { errorMessage ->
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
            }

            viewModel.fetchMenu()

            // Logout Button
            binding.logoutButton.setOnClickListener {
                sharedPreferences.edit().remove("USER_TOKEN").apply()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}