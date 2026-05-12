package com.example.productsStore.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.productsStore.data.local.entity.ProductPreviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductPreviewDao {
    @Upsert
    suspend fun upsertProducts(products: List<ProductPreviewEntity>)

    @Query(
        """
            SELECT * FROM product_previews
            ORDER by id
            LIMIT :limit
        """
    )
    fun observeProducts(limit: Int) : Flow<List<ProductPreviewEntity>>

    @Query("SELECT COUNT(*) FROM product_previews")
    suspend fun getProductsCount() : Int

    @Query("DELETE FROM product_previews")
    suspend fun clearProducts()
}