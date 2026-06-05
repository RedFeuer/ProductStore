package com.example.productsStore.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.productsStore.presentation.elm.productDetails.ProductDetailsNews
import com.example.productsStore.presentation.elm.productDetails.toUiState
import com.example.productsStore.presentation.ui.productDetails.ProductDetailsScreen
import com.example.productsStore.presentation.viewModel.ProductDetailsViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProductDetailsRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: ProductDetailsViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.news.collectLatest { news ->
            when (news) {
                ProductDetailsNews.NavigateBack -> {
                    onBackClick()
                }

                is ProductDetailsNews.ShowMessage -> {

                }
            }
        }
    }

    ProductDetailsScreen(
        state = state.toUiState(),
        onIntent = { intent ->
            viewModel.acceptIntent(intent)
        },
    )
}