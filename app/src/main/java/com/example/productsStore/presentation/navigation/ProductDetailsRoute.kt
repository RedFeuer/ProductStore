package com.example.productsStore.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.productsStore.presentation.ui.ProductDetailsScreen
import com.example.productsStore.presentation.viewModel.ProductDetailsViewModel

@Composable
fun ProductDetailsRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: ProductDetailsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ProductDetailsScreen(
        state = state,
        onBackClick = onBackClick,
        onRetryClick = { viewModel.retryLoadProductDetails() }
    )
}