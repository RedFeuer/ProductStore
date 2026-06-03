package com.example.productsStore.presentation.ui

import android.Manifest
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.productsStore.network.ConnectivityChangeReceiver
import com.example.productsStore.network.NetworkStateHolder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var connectivityChangeReceiver: ConnectivityChangeReceiver
    private var isConnectivityReceiverRegistered: Boolean = false
    @Inject
    lateinit var networkStateHolder: NetworkStateHolder

    private lateinit var requestNotificationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initNotificationPermissionLauncher()
        requestNotificationPermissionIfNeeded()

        connectivityChangeReceiver = ConnectivityChangeReceiver(
            networkStateHolder = networkStateHolder,
        )

        enableEdgeToEdge()
        setContent {
            val isOffline by networkStateHolder.isOffline.collectAsStateWithLifecycle()

            AppRoot(
                isOffline = isOffline,
            )
        }
    }

    override fun onStart() {
        super.onStart()

        networkStateHolder.startMonitoring()
        registerConnectivityReceiver()
    }

    override fun onStop() {
        unregisterConnectivityReceiver()
        networkStateHolder.stopMonitoring()

        super.onStop()
    }

    /** динамическая регистрация Broadcast Receiver для отслеживания состояния сети */
    private fun registerConnectivityReceiver() {
        if (isConnectivityReceiverRegistered) return

        val intentFilter = IntentFilter(
            ConnectivityManager.CONNECTIVITY_ACTION,
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                connectivityChangeReceiver,
                intentFilter,
                Context.RECEIVER_NOT_EXPORTED,
            )
        } else {
            registerReceiver(
                connectivityChangeReceiver,
                intentFilter,
            )
        }

        isConnectivityReceiverRegistered = true
    }

    private fun unregisterConnectivityReceiver() {
        if (!isConnectivityReceiverRegistered) return

        unregisterReceiver(connectivityChangeReceiver)
        isConnectivityReceiverRegistered = false
    }

    /** проверка permissions */
    private fun initNotificationPermissionLauncher() {
        requestNotificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            handleNotificationPermissionResult(isGranted)
        }
    }

    private fun handleNotificationPermissionResult(
        isGranted: Boolean,
    ) {
        val message = if (isGranted) {
            "Уведомления разрешены"
        } else {
            "Без разрешения уведомления о покупках не будут приходить"
        }

        val duration = if (isGranted) {
            Toast.LENGTH_SHORT
        } else {
            Toast.LENGTH_LONG
        }

        Toast.makeText(
            this,
            message,
            duration,
        ).show()
    }

    /** запрос разрешений */
    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val isPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED

        if (!isPermissionGranted) {
            requestNotificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS,
            )
        }
    }
}