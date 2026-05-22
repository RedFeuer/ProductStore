package com.example.productsStore.di

import android.content.Context
import androidx.room.Room
import com.example.productsStore.data.local.dao.CartProductDao
import com.example.productsStore.data.local.dao.ProductDetailsDao
import com.example.productsStore.data.local.dao.ProductPreviewDao
import com.example.productsStore.data.local.database.ProductDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {
    @Provides
    @Singleton
    fun provideProductsDatabase(
        @ApplicationContext context: Context,
    ) : ProductDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = ProductDatabase::class.java,
            name = ProductDatabase.DATABASE_NAME,
        )
            .addMigrations(
                /* миграции */
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideProductDetailsDao(database: ProductDatabase) : ProductDetailsDao {
        return database.productDetailsDao()
    }

    @Provides
    @Singleton
    fun provideProductPreviewDao(database: ProductDatabase) : ProductPreviewDao {
        return database.productPreviewDao()
    }

    @Provides
    @Singleton
    fun provideCartProductDao(database: ProductDatabase) : CartProductDao {
        return database.cartProductDao()
    }
}