package com.example.cofee_shop.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cofee_shop.data.local.database.dao.FavouriteDao
import com.example.cofee_shop.data.local.database.dao.OrderDao

//@Database(
//    entities = [],
//    version = 1,
//    exportSchema = false
//)
//abstract class CoffeeShopDatabase : RoomDatabase() {
//    abstract fun favoriteDao(): FavouriteDao
//    abstract fun orderDao(): OrderDao
//}