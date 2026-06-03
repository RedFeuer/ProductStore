package com.example.productsStore.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.productsStore.domain.model.CartProductModel
import com.example.productsStore.presentation.ui.MainActivity
import com.example.productsstore.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PurchaseReminderReceiver : BroadcastReceiver() {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val productId = intent.getIntExtra(
            PurchaseReminderConstants.EXTRA_PRODUCT_ID,
            UNKNOWN_PRODUCT_ID,
        )
        if (productId == UNKNOWN_PRODUCT_ID) return

        val title = intent.getStringExtra(
            PurchaseReminderConstants.EXTRA_PRODUCT_TITLE,
        ) ?: "Товар"

        val quantity = intent.getIntExtra(
            PurchaseReminderConstants.EXTRA_PRODUCT_QUANTITY,
            0,
        )

        createNotificationChannel(context)

        if (!canShowNotifications(context)) return

        val notification = NotificationCompat.Builder(
            context,
            PurchaseReminderConstants.CHANNEL_ID,
        )
            .setSmallIcon(R.drawable.outline_book_24)
            .setContentTitle(title)
            .setContentText("В вашей корзине: $quantity")
            .setContentIntent(createOpenDetailsPendingIntent(context, productId))
            .addAction(
                0,
                "Открыть",
                createOpenDetailsPendingIntent(context, productId),
            )
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context).notify(
            PurchaseReminderConstants.NOTIFICATION_ID_BASE + productId,
            notification,
        )

        AlarmPurchaseReminderScheduler(context).schedule(
            product = CartProductModel(
                productId = productId,
                title = title,
                price = 0.0,
                brand = null,
                quantity = quantity,
                reminderEnabled = true,
            )
        )
    }

    private fun createOpenDetailsPendingIntent(
        context: Context,
        productId: Int,
    ) = PendingIntent.getActivity(
        context,
        productId,
        Intent(
            Intent.ACTION_VIEW,
            "products-store://product-details/$productId".toUri(),
            context,
            MainActivity::class.java,
        ),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            PurchaseReminderConstants.CHANNEL_ID,
            PurchaseReminderConstants.CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT,
        )

        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    private fun canShowNotifications(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }

        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val UNKNOWN_PRODUCT_ID = -1

        fun createIntent(
            context: Context,
            productId: Int,
            title: String,
            quantity: Int,
        ): Intent {
            return Intent(context, PurchaseReminderReceiver::class.java).apply {
                putExtra(PurchaseReminderConstants.EXTRA_PRODUCT_ID, productId)
                putExtra(PurchaseReminderConstants.EXTRA_PRODUCT_TITLE, title)
                putExtra(PurchaseReminderConstants.EXTRA_PRODUCT_QUANTITY, quantity)
            }
        }
    }
}