package com.example.productsStore.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.productsStore.presentation.ui.CartScreen
import com.example.productsStore.presentation.viewModel.CartViewModel

@Composable
fun CartRoute(
    onBackClick: () -> Unit,
    onProductClick: (Int) -> Unit,
) {
    val viewModel: CartViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    CartScreen(
        state = state,
        onBackClick = onBackClick,
        onProductClick = onProductClick,
        onClearCartClick = { viewModel.clearCart() },
    )
}