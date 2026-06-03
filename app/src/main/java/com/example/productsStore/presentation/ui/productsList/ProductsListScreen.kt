package com.example.productsStore.presentation.ui.productsList

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.productsStore.presentation.state.ProductListUiState
import com.example.productsstore.R

/** обработчик состояний */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsListScreen(
    state: ProductListUiState,
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onPageSizeCalculated: (Int) -> Unit,
    onLoadNextPage:() -> Unit,
    onRetryInitialLoadingClick: () -> Unit,
    onRetryNextPageClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.products),
                        fontWeight = FontWeight.Bold,
                    )
                },
                actions = {
                    Button(
                        onClick = onCartClick,
                    ) {
                        Text(text = stringResource(R.string.cart))
                    }
                }
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val pageSize = rememberCalculatedPageSize(
                viewportHeight = maxHeight,
            )

            /* вызываем только при изменении pageSize */
            LaunchedEffect(pageSize) {
                onPageSizeCalculated(pageSize)
            }

            when(state) {
                is ProductListUiState.Success -> {
                    ProductsListContent(
                        state = state,
                        pageSize = pageSize,
                        onProductClick = onProductClick,
                        onLoadNextPage = onLoadNextPage,
                        onRetryNextPageClick = onRetryNextPageClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                ProductListUiState.Loading -> {
                    LoadingContent(
                        modifier = Modifier.fillMaxSize()
                    )
                }

                ProductListUiState.Empty -> {
                    EmptyContent(
                        text = stringResource(R.string.products_list_is_empty),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is ProductListUiState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetryClick = onRetryInitialLoadingClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}