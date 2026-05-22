package com.example.productsStore.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.productsStore.data.local.dao.CartProductDao
import com.example.productsStore.data.local.dao.ProductDetailsDao
import com.example.productsStore.data.local.dao.ProductPreviewDao
import com.example.productsStore.data.local.entity.CartProductEntity
import com.example.productsStore.data.local.entity.ProductDetailsEntity
import com.example.productsStore.data.local.entity.ProductPreviewEntity

@Database(
    entities = [
        ProductDetailsEntity::class,
        ProductPreviewEntity::class,
        CartProductEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class ProductDatabase : RoomDatabase() {
    abstract fun productPreviewDao() : ProductPreviewDao
    abstract fun productDetailsDao() : ProductDetailsDao
    abstract fun cartProductDao() : CartProductDao

    companion object {
        const val DATABASE_NAME = "products_database"
    }
}