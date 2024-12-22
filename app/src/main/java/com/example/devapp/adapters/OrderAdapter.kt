package com.example.devapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.devapp.R
import com.example.devapp.database.models.Order

class OrderAdapter(private val orders: List<Order>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    // ViewHolder для одного элемента списка
    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderId: TextView = view.findViewById(R.id.tvOrderId)
        val tvOrderDate: TextView = view.findViewById(R.id.tvOrderDate)
        val tvOrderPrice: TextView = view.findViewById(R.id.tvOrderPrice)
    }

    // Создание нового ViewHolder и привязка макета
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    // Привязка данных к элементу списка
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.tvOrderId.text = "Order ID: ${order.clientid}"
        holder.tvOrderDate.text = "Дата: ${order.orderdate}"
        holder.tvOrderPrice.text = "Стоимость: ${order.price}"
    }

    // Возвращаем количество элементов в списке
    override fun getItemCount(): Int {
        return orders.size
    }
}
