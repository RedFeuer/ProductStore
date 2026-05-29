package com.example.productsStore.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
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
import com.example.productsStore.network.NetworkStateHolder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var networkStateHolder: NetworkStateHolder

    private lateinit var requestNotificationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initNotificationPermissionLauncher()
        requestNotificationPermissionIfNeeded()

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
    }

    override fun onStop() {
        networkStateHolder.stopMonitoring()
        super.onStop()
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