package com.example.productsStore.di

import com.example.productsStore.reminder.AlarmPurchaseReminderScheduler
import com.example.productsStore.reminder.PurchaseReminderScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReminderModule {
    @Binds
    @Singleton
    abstract fun bindPurchaseReminderScheduler(impl: AlarmPurchaseReminderScheduler): PurchaseReminderScheduler
}