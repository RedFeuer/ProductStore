package com.example.productsStore.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.productsStore.presentation.elm.productsList.ProductsListNews
import com.example.productsStore.presentation.elm.productsList.toUiState
import com.example.productsStore.presentation.ui.productsList.ProductsListScreen
import com.example.productsStore.presentation.viewModel.ProductsListViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProductsListRoute(
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
) {
    val viewModel: ProductsListViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.news.collectLatest { news ->
            when (news) {
                is ProductsListNews.OpenProductsDetails -> {
                    onProductClick(news.productId)
                }

                ProductsListNews.OpenCart -> {
                    onCartClick()
                }
            }
        }
    }

    ProductsListScreen(
        state = state.toUiState(),
        onIntent = { intent ->
            viewModel.acceptIntent(intent)
        }
//        onProductClick = { productId ->
//            viewModel.acceptIntent(
//                ProductsListIntent.ProductClicked(productId)
//            )
//        },
//        onCartClick = {
//            viewModel.acceptIntent(
//                ProductsListIntent.CartClicked
//            )
//        },
//        onPageSizeCalculated = { pageSize ->
//            viewModel.acceptIntent(
//                ProductsListIntent.PageSizeCalculated(pageSize)
//            )
//        },
//        onLoadNextPage = {
//            viewModel.acceptIntent(
//                ProductsListIntent.LoadNextPage
//            )
//        },
//        onRetryInitialLoadingClick = {
//            viewModel.acceptIntent(
//                ProductsListIntent.RetryInitialLoading
//            )
//        },
//        onRetryNextPageClick = {
//            viewModel.acceptIntent(
//                ProductsListIntent.RetryNextPage
//            )
//        }
    )
}