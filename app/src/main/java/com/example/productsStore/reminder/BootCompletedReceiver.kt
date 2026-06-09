package com.example.productsStore.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.productsStore.domain.useCase.GetProductsWithEnabledRemindersUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {
    @Inject
    lateinit var getProductsWithEnabledRemindersUseCase: GetProductsWithEnabledRemindersUseCase

    @Inject
    lateinit var purchaseReminderScheduler: PurchaseReminderScheduler

    private val scope = CoroutineScope(SupervisorJob())

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()

        scope.launch {
            try {
                val productsWithReminders = getProductsWithEnabledRemindersUseCase()

                productsWithReminders.forEach { productModel ->
                    purchaseReminderScheduler.schedule(productModel)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}