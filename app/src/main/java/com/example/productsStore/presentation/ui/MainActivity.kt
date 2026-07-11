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
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var requestNotificationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initNotificationPermissionLauncher()
        requestNotificationPermissionIfNeeded()

        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
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