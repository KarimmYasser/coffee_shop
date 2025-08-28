package com.example.cofee_shop.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cofee_shop.R
import com.example.cofee_shop.data.local.database.entities.OrderEntity
import java.text.SimpleDateFormat
import java.util.*

class OrdersAdapter : ListAdapter<OrderEntity, OrdersAdapter.OrderViewHolder>(OrderDiffCallback()) {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textOrderId: TextView = itemView.findViewById(R.id.textOrderId)
        private val textOrderTotal: TextView = itemView.findViewById(R.id.textOrderTotal)
        private val textOrderDate: TextView = itemView.findViewById(R.id.textOrderDate)
        private val textItemCount: TextView = itemView.findViewById(R.id.textItemCount)

        fun bind(order: OrderEntity) {
            textOrderId.text = "Order #${order.orderId}"
            textOrderTotal.text = "$${order.totalAmount}"


            val sdf = SimpleDateFormat("EEE, dd MMM yyyy • hh:mm a", Locale.getDefault())
            val dateStr = sdf.format(Date(order.placedAt))
            textOrderDate.text = dateStr


            textItemCount.text = "0 items"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class OrderDiffCallback : DiffUtil.ItemCallback<OrderEntity>() {
    override fun areItemsTheSame(oldItem: OrderEntity, newItem: OrderEntity): Boolean {
        return oldItem.orderId == newItem.orderId
    }

    override fun areContentsTheSame(oldItem: OrderEntity, newItem: OrderEntity): Boolean {
        return oldItem == newItem
    }
}
