package com.example.productsStore.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

class ConnectivityChangeReceiver(
    private val networkStateHolder: NetworkStateHolder,
) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            networkStateHolder.refreshFromConnectivityBroadcast()
        }
    }
}