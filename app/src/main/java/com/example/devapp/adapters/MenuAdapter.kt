package com.example.devapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.devapp.R
import com.example.devapp.database.api.ApiClient
import com.example.devapp.database.models.Menu
import com.example.devapp.databinding.ItemMenuBinding
import com.example.devapp.views.MenuViewModel
import kotlinx.coroutines.launch

class MenuAdapter(
    private var menuList: List<Menu>,
    private val onDishSelected: (Menu, Int) -> Unit,
    private val isAdmin: Boolean,
    private val menuViewModel: MenuViewModel
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
                onDishSelected(menu, newQuantity)
                notifyItemChanged(position)
            }

            // Кнопка - (уменьшение количества)
            binding.btnDecrease.setOnClickListener {
                if (quantity > 0) {
                    val newQuantity = quantity - 1
                    dishQuantities[menu.iddish] = newQuantity
                    binding.tvDishQuantity.text = "Количество: $newQuantity"
                    onDishSelected(menu, newQuantity)
                    notifyItemChanged(position)
                }
            }

            // Кнопка удаления блюда (видна только для администраторов)
            binding.btnDeleteDish.visibility = if (isAdmin) View.VISIBLE else View.GONE
            binding.btnDeleteDish.visibility = if (isAdmin) View.VISIBLE else View.GONE


            // Обработчик нажатия на кнопку удаления
            binding.btnDeleteDish.setOnClickListener {
                menuViewModel.deleteDish(menu.iddish)  // Call the delete function from the ViewModel
            }
        }
    }

}

