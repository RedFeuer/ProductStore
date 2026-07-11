package com.example.productsStore.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.SystemClock
import com.example.productsStore.domain.model.CartProductModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmPurchaseReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) : PurchaseReminderScheduler {
    private val alarmManager: AlarmManager =
        context.getSystemService(AlarmManager::class.java)

    override fun schedule(product: CartProductModel) {
        val pendingIntent = createPendingIntent(
            productId = product.productId,
            title = product.title,
            quantity = product.quantity,
        )

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + PurchaseReminderConstants.REMINDER_INTERVAL_MILLIS,
            pendingIntent,
        )
    }

    override fun cancel(productId: Int) {
        val pendingIntent = createPendingIntent(
            productId = productId,
            title = "",
            quantity = 0,
        )

        alarmManager.cancel(pendingIntent)
    }

    private fun createPendingIntent(
        productId: Int,
        title: String,
        quantity: Int,
    ): PendingIntent {
        val intent = PurchaseReminderReceiver.createIntent(
            context = context,
            productId = productId,
            title = title,
            quantity = quantity,
        )

        return PendingIntent.getBroadcast(
            context,
            productId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}