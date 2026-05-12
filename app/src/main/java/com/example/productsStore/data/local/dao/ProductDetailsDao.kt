package com.example.productsStore.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.productsStore.data.local.entity.ProductDetailsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDetailsDao {
    @Upsert
    suspend fun upsertProductDetails(product: ProductDetailsEntity)

    @Query(
        """
            SELECT * FROM product_details
            WHERE id = :id
            LIMIT 1
        """
    )
    fun observeProductDetailsById(id: Int): Flow<ProductDetailsEntity?>

    @Query(
        """
            SELECT * FROM product_details
            WHERE id = :id
            LIMIT 1
        """
    )
    suspend fun getProductDetailsById(id: Int): ProductDetailsEntity?
}