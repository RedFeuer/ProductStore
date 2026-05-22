package com.example.productsStore.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_details")
data class ProductDetailsEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val price: Double,
    val brand: String?,

    val description: String,
    val rating: Double,
    val weight: Int,
    val availabilityStatus: String, // можно потом отдельным классом сделать, в API два состояния всего
    val warrantyInformation: String,

    val imageUrl: String?,
    val loadedAtMillis: Long, // для контроля актуальности кэша
)
