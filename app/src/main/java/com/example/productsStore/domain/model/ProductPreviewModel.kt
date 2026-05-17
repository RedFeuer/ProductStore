package com.example.productsStore.domain.model

/** модель товара для экрана списка товаров с отображением минимальной необходимой информации */
data class ProductPreviewModel(
    val id : Int,
    val title : String,
    val price : Double,
    val brand : String?,
)
