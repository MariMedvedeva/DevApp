package com.example.devapp.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devapp.adapters.MenuAdapter
import com.example.devapp.databinding.ActivityMenuBinding
import com.example.devapp.views.MenuViewModel

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private val menuViewModel: MenuViewModel by viewModels()
    private lateinit var menuAdapter: MenuAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        menuAdapter = MenuAdapter(emptyList())
        binding.rvMenu.layoutManager = LinearLayoutManager(this)
        binding.rvMenu.adapter = menuAdapter

        menuViewModel.menuDishes.observe(this, Observer { dishes ->
            dishes?.let {
                menuAdapter.setMenuList(it)
            }
        })
    }
}