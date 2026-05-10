package com.example.productsStore.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.productsStore.presentation.ui.ProductsListScreen
import com.example.productsStore.presentation.viewModel.ProductsListViewModel

@Composable
fun ProductsListRoute(
    onProductClick: (Int) -> Unit,
) {
    val viewModel : ProductsListViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ProductsListScreen(
        state = state,
        onProductClick = onProductClick,
        onRetryClick = { viewModel.loadProducts() },
    )
}