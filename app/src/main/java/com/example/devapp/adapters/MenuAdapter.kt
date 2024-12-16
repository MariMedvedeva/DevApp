package com.example.devapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.devapp.R
import com.example.devapp.database.models.Menu

class MenuAdapter(private var dishes: List<Menu>) :
    RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dishName: TextView = view.findViewById(R.id.tvDishName)
        val price: TextView = view.findViewById(R.id.tvPrice)
        val description: TextView = view.findViewById(R.id.tvDescription)
    }

    fun setMenuList(newDishes: List<Menu>) {
        dishes = newDishes
        notifyDataSetChanged() // Уведомление о том, что данные обновлены
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.menu_item, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val dish = dishes[position]
        holder.dishName.text = dish.namedish
        holder.price.text = "Цена: ${dish.price} ₽"
        holder.description.text = dish.description
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    fun updateMenuList(newDishes: List<Menu>) {
        dishes = newDishes
        notifyDataSetChanged() // Уведомление о том, что данные обновлены
    }
}