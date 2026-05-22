package com.example.productsStore.domain.model

/** детальная модель товара для экрана товара с флагом устарел ли кэш */
data class CachedProductDetailsModel(
    val product: ProductDetailsModel,
    val isStale: Boolean, // true - устаревший, false - свежий (24 часа)
)
