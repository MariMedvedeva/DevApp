package com.example.devapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.devapp.R
import com.example.devapp.database.models.Menu
import com.example.devapp.databinding.ItemMenuBinding

class MenuAdapter(
    private var menuList: List<Menu>,
    private val onDishSelected: (Menu, Int) -> Unit
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private val dishQuantities = mutableMapOf<Int, Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu = menuList[position]
        holder.bind(menu, position)
    }

    override fun getItemCount(): Int = menuList.size

    fun setMenuList(dishes: List<Menu>) {
        menuList = dishes
        notifyDataSetChanged()
    }

    inner class MenuViewHolder(private val binding: ItemMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(menu: Menu, position: Int) {
            binding.tvDishName.text = menu.namedish
            binding.tvDishPrice.text = "${menu.price} ₽"

            // Получаем количество из dishQuantities
            val quantity = dishQuantities[menu.iddish] ?: 0
            binding.tvDishQuantity.text = "Количество: $quantity"

            // Кнопка + (увеличение количества)
            binding.btnIncrease.setOnClickListener {
                val newQuantity = quantity + 1
                dishQuantities[menu.iddish] = newQuantity
                binding.tvDishQuantity.text = "Количество: $newQuantity"
                onDishSelected(menu, newQuantity) // Обновляем в Activity
                notifyItemChanged(position) // Обновляем только этот элемент
            }

            // Кнопка - (уменьшение количества)
            binding.btnDecrease.setOnClickListener {
                if (quantity > 0) {
                    val newQuantity = quantity - 1
                    dishQuantities[menu.iddish] = newQuantity
                    binding.tvDishQuantity.text = "Количество: $newQuantity"
                    onDishSelected(menu, newQuantity) // Обновляем в Activity
                    notifyItemChanged(position) // Обновляем только этот элемент
                }
            }
        }
    }
}

