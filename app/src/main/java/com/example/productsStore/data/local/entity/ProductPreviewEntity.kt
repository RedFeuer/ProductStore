package com.example.productsStore.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_previews")
data class ProductPreviewEntity(
    @PrimaryKey
    val id : Int,
    val title : String,
    val price : Double,
    val brand : String?,
)
