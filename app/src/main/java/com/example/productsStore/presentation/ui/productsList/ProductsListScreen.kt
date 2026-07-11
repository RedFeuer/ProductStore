package com.example.productsStore.presentation.ui.productsList

import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.productsStore.presentation.elm.productsList.ProductsListIntent
import com.example.productsStore.presentation.state.ProductListUiState
import com.example.productsstore.R

/** обработчик состояний */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsListScreen(
    state: ProductListUiState,
    onIntent: (ProductsListIntent) -> Unit,
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
                        onClick = { onIntent(ProductsListIntent.CartClicked) },
                    ) {
                        Text(text = stringResource(R.string.cart))
                    }
                }
            )
        }
    ) { innerPadding ->

        var viewportHeightPx by remember {
            mutableIntStateOf(0)
        }

        val pageSize = rememberCalculatedPageSize(
            viewportHeightPx = viewportHeightPx,
        )

        LaunchedEffect(pageSize) {
            if (pageSize > 0) {
                onIntent(ProductsListIntent.PageSizeCalculated(pageSize))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .onSizeChanged { size ->
                    viewportHeightPx = size.height
                }
        ) {
            when(state) {
                is ProductListUiState.Success -> {
                    ProductsListContent(
                        state = state,
                        pageSize = pageSize.coerceAtLeast(1),
                        onIntent = onIntent,
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
                        onIntent = onIntent,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}