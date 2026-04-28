package com.example.e_commerce_app.di

import android.content.Context
import androidx.room.Room
import com.example.e_commerce_app.data.local.ShoplyDatabase
import com.example.e_commerce_app.data.local.dao.CartDao
import com.example.e_commerce_app.data.local.dao.OrderDao
import com.example.e_commerce_app.data.local.dao.ProductDao
import com.example.e_commerce_app.data.local.dao.WishlistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ShoplyDatabase =
        Room.databaseBuilder(context, ShoplyDatabase::class.java, ShoplyDatabase.NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideProductDao(db: ShoplyDatabase): ProductDao = db.productDao()

    @Provides
    fun provideCartDao(db: ShoplyDatabase): CartDao = db.cartDao()

    @Provides
    fun provideWishlistDao(db: ShoplyDatabase): WishlistDao = db.wishlistDao()

    @Provides
    fun provideOrderDao(db: ShoplyDatabase): OrderDao = db.orderDao()
}