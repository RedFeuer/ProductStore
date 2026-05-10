package com.example.productsStore.presentation.ui

import androidx.compose.runtime.Composable
import com.example.productsStore.presentation.navigation.AppNavigationGraph
import com.example.productsStore.presentation.theme.ProductsStoreTheme

@Composable
fun AppRoot() {
    ProductsStoreTheme {
        AppNavigationGraph()
    }
}