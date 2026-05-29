package com.example.productsStore.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PurchaseReminderReceiver : BroadcastReceiver() {
    companion object{
        private const val UNKNOWN_PRODUCT_ID = -1

        fun createIntent(
            context: Context,
            productId: Int,
            title: String,
            quantity: Int,
        ) : Intent {
            return Intent(context, PurchaseReminderReceiver::class.java).apply {
                putExtra(PurchaseReminderConstants.EXTRA_PRODUCT_ID, productId)
                putExtra(PurchaseReminderConstants.EXTRA_PRODUCT_TITLE, title)
                putExtra(PurchaseReminderConstants.EXTRA_PRODUCT_QUANTITY, quantity)
            }
        }
    }
}