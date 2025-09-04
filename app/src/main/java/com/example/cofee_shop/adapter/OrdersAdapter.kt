package com.example.cofee_shop.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cofee_shop.R
import com.example.cofee_shop.data.local.database.entities.OrderEntity
import com.example.cofee_shop.data.local.database.entities.OrderItemEntity
import java.text.SimpleDateFormat
import java.util.*

class OrdersAdapter(
    private val onOrderClick: (OrderEntity) -> Unit,
    private val onDeleteClick: (OrderEntity) -> Unit,
    private val getOrderItems: (String) -> List<OrderItemEntity>
) : ListAdapter<OrderEntity, OrdersAdapter.OrderViewHolder>(OrderDiffCallback()) {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textOrderId: TextView = itemView.findViewById(R.id.textOrderId)
        private val textOrderTotal: TextView = itemView.findViewById(R.id.textOrderTotal)
        private val textOrderDate: TextView = itemView.findViewById(R.id.textOrderDate)
        private val textItemCount: TextView = itemView.findViewById(R.id.textItemCount)
        private val buttonDelete: ImageButton = itemView.findViewById(R.id.buttonDelete)
        private val orderItemsContainer: LinearLayout = itemView.findViewById(R.id.orderItemsContainer)

        fun bind(order: OrderEntity) {
            textOrderId.text = "Order #${order.orderId.takeLast(6)}"
            textOrderTotal.text = "Rp ${String.format("%.0f", order.totalAmount)}"

            // Format date
            val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
            val dateStr = sdf.format(Date(order.placedAt))
            textOrderDate.text = dateStr

            // Get order items and display count
            val items = getOrderItems(order.orderId)
            textItemCount.text = "${items.size} item${if (items.size != 1) "s" else ""}"

            // Set status colors and text
            updateOrderStatus(order)
            updatePaymentStatus(order)

            // Display order items
            displayOrderItems(items)

            // Set click listeners
            itemView.setOnClickListener {
                onOrderClick(order)
            }

            buttonDelete.setOnClickListener {
                onDeleteClick(order)
            }

            // Hide delete button for completed orders
            buttonDelete.visibility = if (order.status == "completed") View.GONE else View.VISIBLE
        }

        private fun updateOrderStatus(order: OrderEntity) {


            val statusText = order.status.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }

        }

        private fun updatePaymentStatus(order: OrderEntity) {



        }

        private fun displayOrderItems(items: List<OrderItemEntity>) {
            orderItemsContainer.removeAllViews()

            items.take(3).forEach { item -> // Show only first 3 items
                val itemView = LayoutInflater.from(itemView.context)
                    .inflate(R.layout.item_order_detail, orderItemsContainer, false)

                val textItemName = itemView.findViewById<TextView>(R.id.textItemName)
                val textItemQuantity = itemView.findViewById<TextView>(R.id.textItemQuantity)
                val textItemPrice = itemView.findViewById<TextView>(R.id.textItemPrice)

                textItemName.text = item.coffeeName
                textItemQuantity.text = "${item.quantity}x"
                textItemPrice.text = "Rp ${String.format("%.0f", item.price * item.quantity)}"

                orderItemsContainer.addView(itemView)
            }

            // Show "and X more" if there are more items
            if (items.size > 3) {
                val moreView = LayoutInflater.from(itemView.context)
                    .inflate(R.layout.item_order_more, orderItemsContainer, false)

                val textMoreItems = moreView.findViewById<TextView>(R.id.textMoreItems)
                textMoreItems.text = "and ${items.size - 3} more..."

                orderItemsContainer.addView(moreView)
            }
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