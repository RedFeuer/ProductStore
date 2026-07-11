package com.example.productsStore.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.productsStore.presentation.navigation.AppNavigationGraph
import com.example.productsStore.presentation.theme.ProductsStoreTheme
import com.example.productsstore.R

@Composable
fun AppRoot(
    isOffline: Boolean,
) {
    ProductsStoreTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            if (isOffline) {
                OfflineIndicator()
            }

            Box(
                modifier = Modifier.weight(1f),
            ) {
                AppNavigationGraph()
            }
        }
    }
}

@Composable
private fun OfflineIndicator(
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.offline_mode),
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.errorContainer)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        color = MaterialTheme.colorScheme.onErrorContainer,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
    )
}