package com.example.cofee_shop.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cofee_shop.data.local.database.dao.CoffeeDao
import com.example.cofee_shop.data.local.database.dao.FavoriteDao
import com.example.cofee_shop.data.local.database.dao.OrderDao
import com.example.cofee_shop.data.local.database.entities.CoffeeEntity
import com.example.cofee_shop.data.local.database.entities.FavoriteEntity
import com.example.cofee_shop.data.local.database.entities.OrderEntity
import com.example.cofee_shop.data.local.database.entities.OrderItemEntity

@Database(
    entities = [CoffeeEntity::class, FavoriteEntity::class, OrderEntity::class, OrderItemEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CoffeeShopDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
    abstract fun coffeeDao(): CoffeeDao
    abstract fun favoriteDao(): FavoriteDao
}
