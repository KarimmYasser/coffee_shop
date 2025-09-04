package com.example.cofee_shop.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.cofee_shop.data.local.database.entities.OrderEntity
import com.example.cofee_shop.data.local.database.entities.OrderItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Query("SELECT * FROM orders ORDER BY placedAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE status = 'pending' OR status = 'paid' ORDER BY placedAt DESC")
    fun getRecentOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE status = 'completed' OR status = 'cancelled' ORDER BY placedAt DESC")
    fun getPastOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItems(orderId: String): List<OrderItemEntity>

    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    suspend fun getOrderById(orderId: String): OrderEntity?

    @Insert
    suspend fun insertOrder(order: OrderEntity)

    @Insert
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Transaction
    suspend fun insertOrderWithItems(order: OrderEntity, items: List<OrderItemEntity>) {
        insertOrder(order)
        insertOrderItems(items)
    }

    @Transaction
    suspend fun updateOrderStatus(orderId: String, status: String, paymentStatus: String) {
        val order = getOrderById(orderId)
        order?.let {
            val updatedOrder = it.copy(status = status, paymentStatus = paymentStatus)
            updateOrder(updatedOrder)
        }
    }

    @Query("DELETE FROM orders WHERE orderId = :orderId")
    suspend fun deleteOrder(orderId: String)

    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    suspend fun deleteOrderItems(orderId: String)

    @Transaction
    suspend fun deleteOrderWithItems(orderId: String) {
        deleteOrderItems(orderId)
        deleteOrder(orderId)
    }
}
