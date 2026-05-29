package com.example.productsStore.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkStateHolder @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val connectivityManager: ConnectivityManager =
        context.getSystemService(ConnectivityManager::class.java)

    private val _isOffline = MutableStateFlow<Boolean>(!hasInternetConnection())
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    fun refresh() {
        _isOffline.value = !hasInternetConnection()
    }

    private fun hasInternetConnection(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false

        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}