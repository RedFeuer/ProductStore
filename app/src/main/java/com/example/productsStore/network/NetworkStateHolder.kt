package com.example.productsStore.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkStateHolder @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val connectivityManager: ConnectivityManager =
        context.getSystemService(ConnectivityManager::class.java)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _isOffline = MutableStateFlow<Boolean>(!hasValidatedInternetConnection())
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    private var isMonitoringStarted: Boolean = false
    private var showOfflineJob: Job? = null

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            updateOfflineState(
                isOffline = !hasValidatedInternetConnection(),
            )
        }

        override fun onLost(network: Network) {
            updateOfflineState(
                isOffline = true,
            )
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            updateOfflineState(
                isOffline = !networkCapabilities.hasValidatedInternetConnection(),
            )
        }

        override fun onUnavailable() {
            updateOfflineState(
                isOffline = true,
            )
        }
    }

    fun refreshFromConnectivityBroadcast() {
        updateOfflineState(
            isOffline = !hasValidatedInternetConnection()
        )
    }

    fun startMonitoring() {
        if (isMonitoringStarted) return

        connectivityManager.registerDefaultNetworkCallback(networkCallback)
        isMonitoringStarted = true

        updateOfflineState(
            isOffline = !hasValidatedInternetConnection(),
        )
    }

    fun stopMonitoring() {
        if (!isMonitoringStarted) return

        showOfflineJob?.cancel()
        showOfflineJob = null

        connectivityManager.unregisterNetworkCallback(networkCallback)
        isMonitoringStarted = false
    }

    private fun updateOfflineState(
        isOffline: Boolean,
    ) {
        if (isOffline) {
            showOfflineJob?.cancel()

            showOfflineJob = scope.launch {
                delay(OFFLINE_INDICATOR_DELAY_MILLIS)

                if (!hasValidatedInternetConnection()) {
                    _isOffline.value = true
                }
            }
        } else {
            showOfflineJob?.cancel()
            showOfflineJob = null

            _isOffline.value = false
        }
    }

    private fun hasValidatedInternetConnection(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false

        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return networkCapabilities.hasValidatedInternetConnection()
    }

    private fun NetworkCapabilities.hasValidatedInternetConnection(): Boolean {
        return hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private companion object {
        const val OFFLINE_INDICATOR_DELAY_MILLIS = 800L
    }
}