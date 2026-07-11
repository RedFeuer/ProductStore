package com.example.productsStore.reminder

import com.example.productsStore.domain.model.CartProductModel

interface PurchaseReminderScheduler {
    fun schedule(product: CartProductModel)
    fun cancel(productId: Int)
}