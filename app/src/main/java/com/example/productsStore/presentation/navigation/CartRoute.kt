package com.example.productsStore.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.productsStore.presentation.elm.cart.CartIntent
import com.example.productsStore.presentation.elm.cart.CartNews
import com.example.productsStore.presentation.ui.CartScreen
import com.example.productsStore.presentation.viewModel.CartViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CartRoute(
    onBackClick: () -> Unit,
    onProductClick: (Int) -> Unit,
) {
    val viewModel: CartViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.news.collectLatest { news ->
            when (news) {
                CartNews.NavigateBack -> {
                    onBackClick()
                }

                is CartNews.OpenProductDetails -> {
                    onProductClick(news.productId)
                }

                is CartNews.ShowMessage -> {
                    // TODO: добавить показ сообщений
                }
            }
        }
    }

    CartScreen(
        state = state,
        onBackClick = {
            viewModel.acceptIntent(CartIntent.BackClicked)
        },
        onProductClick = { productId ->
            viewModel.acceptIntent(
                CartIntent.ProductClicked(productId = productId)
            )
        },
        onClearCartClick = {
            viewModel.acceptIntent(CartIntent.ClearCartClicked)
        },
    )
}