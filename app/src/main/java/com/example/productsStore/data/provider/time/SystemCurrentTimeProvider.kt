package com.example.productsStore.data.provider.time

import com.example.productsStore.domain.provider.time.CurrentTimeProvider
import javax.inject.Inject

class SystemCurrentTimeProvider @Inject constructor() : CurrentTimeProvider {
    override fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}