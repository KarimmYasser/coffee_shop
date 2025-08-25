//package com.example.cofee_shop.core.di
//
//import android.content.Context
//import androidx.room.Room
//import com.example.cofee_shop.data.local.database.CoffeeShopDatabase
//import com.example.cofee_shop.data.local.database.dao.FavouriteDao
//import com.example.cofee_shop.data.local.database.dao.OrderDao
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//object DatabaseModule {
//
//    @Provides
//    @Singleton
//    fun provideCoffeeShopDatabase(@ApplicationContext context: Context): CoffeeShopDatabase {
//        return Room.databaseBuilder(
//            context,
//            CoffeeShopDatabase::class.java,
//            "coffee_shop_database"
//        ).build()
//    }
//
//    @Provides
//    fun provideFavoriteDao(database: CoffeeShopDatabase): FavouriteDao {
//        return database.favoriteDao()
//    }
//
//    @Provides
//    fun provideOrderDao(database: CoffeeShopDatabase): OrderDao {
//        return database.orderDao()
//    }
//}