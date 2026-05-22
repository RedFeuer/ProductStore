package com.example.productsStore.domain.provider.time

interface CurrentTimeProvider {
    fun currentTimeMillis(): Long
}