package com.example.productsStore.reminder

object PurchaseReminderConstants {
    const val CHANNEL_ID = "purchase_reminders"
    const val CHANNEL_NAME = "Напоминание о покупках"

    const val EXTRA_PRODUCT_ID = "extra_product_id"
    const val EXTRA_PRODUCT_TITLE = "extra_product_title"
    const val EXTRA_PRODUCT_QUANTITY = "extra_product_quantity"

    const val REMINDER_INTERVAL_MILLIS = 60L * 60L * 1000L // 1 час
    const val NOTIFICATION_ID_BASE = 10_000
}