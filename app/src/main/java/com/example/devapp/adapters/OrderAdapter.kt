package com.example.devapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.devapp.R
import com.example.devapp.database.models.Order
import com.example.devapp.database.models.OrderResponse

class OrderAdapter(
    private val orders: List<OrderResponse>,
    private val onChangeStatus: (Int) -> Unit,
    private val onCancelOrder: (Int) -> Unit,
    private val isAdmin: Boolean
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderId: TextView = view.findViewById(R.id.tvOrderId)
        val tvOrderDate: TextView = view.findViewById(R.id.tvOrderDate)
        val tvOrderPrice: TextView = view.findViewById(R.id.tvOrderPrice)
        val tvOrderStatus: TextView = view.findViewById(R.id.tvOrderStatus)
        val btnChangeStatus: Button = view.findViewById(R.id.btnChangeStatus)
        val btnCancelOrder: Button = view.findViewById(R.id.btnCancelOrder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.tvOrderId.text = "Клиент: ${order.clientName}"
        holder.tvOrderDate.text = "Дата: ${order.formattedOrderDate}"
        holder.tvOrderPrice.text = "Стоимость: ${order.price} руб."
        holder.tvOrderStatus.text = "Статус: ${order.statusName}"
        if (!isAdmin) {
            holder.btnChangeStatus.visibility = View.GONE
            holder.btnCancelOrder.visibility = View.GONE
        } else {
            // Если пользователь администратор, показать кнопки и обработать клики
            holder.btnChangeStatus.setOnClickListener {
                onChangeStatus(order.idorder)  // Вызов функции для изменения статуса
            }

            holder.btnCancelOrder.setOnClickListener {
                onCancelOrder(order.idorder)  // Вызов функции для отмены заказа
            }
        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }
}

