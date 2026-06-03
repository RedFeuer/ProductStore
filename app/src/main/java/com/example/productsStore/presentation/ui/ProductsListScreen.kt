package com.example.productsStore.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.productsStore.domain.model.ProductPreviewModel
import com.example.productsStore.presentation.state.ProductListUiState
import com.example.productsstore.R
import java.util.Locale
import kotlin.math.floor

private val ProductCardHeight = 120.dp
private val ProductCardSpacing = 12.dp

private const val PageSizeMultiplier = 2
private const val PrefetchDivider = 4

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

/** список товаров */
@Composable
private fun ProductsListContent(
    state: ProductListUiState.Success,
    pageSize: Int,
    onProductClick: (Int) -> Unit,
    onLoadNextPage: () -> Unit,
    onRetryNextPageClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    val shouldLoadNextPage by remember(
        state.products.size,
        state.isPageLoading,
        state.endReached,
        state.pageErrorMessage,
        pageSize
    ) {
        derivedStateOf {
            shouldLoadNextPage(
                listState = listState,
                productsCount = state.products.size,
                pageSize = pageSize,
                isPageLoading = state.isPageLoading,
                endReached = state.endReached,
                pageErrorMessage = state.pageErrorMessage,
            )
        }
    }

    LaunchedEffect(shouldLoadNextPage) {
        if (shouldLoadNextPage) {
            onLoadNextPage()
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = state.products,
            key = { product -> product.id },
        ) { product ->
            ProductCard(
                product = product,
                onClick = { onProductClick(product.id) },
            )
        }

        if (state.isPageLoading) {
            item(key = ProductsListItemKeys.PAGE_LOADING) {
                PageLoadingContent()
            }
        }

        if (state.pageErrorMessage != null) {
            item(key = ProductsListItemKeys.PAGE_ERROR) {
                PageErrorContent(
                    message = state.pageErrorMessage,
                    onRetryClick = onRetryNextPageClick,
                )
            }
        }

        if (state.endReached) {
            item(key = ProductsListItemKeys.END_REACHED) {
                EndReachedContent()
            }
        }
    }
}

@Composable
private fun PageLoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PageErrorContent(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )

        Button(
            onClick = onRetryClick
        ) {
            Text(text = stringResource(R.string.repeat))
        }
    }
}

@Composable
private fun EndReachedContent(
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.all_products_are_loaded),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

/** отвечает за пагинацию. начинает подгрузку следующих N элементов,
 * когда пользователь прокручивает список до элемента N - N/4 */
private fun shouldLoadNextPage(
    listState: LazyListState,
    productsCount: Int,
    pageSize: Int,
    isPageLoading: Boolean,
    endReached: Boolean,
    pageErrorMessage: String?,
) : Boolean {
    if (productsCount == 0) return false
    if (isPageLoading) return false
    if (endReached) return false
    if (pageErrorMessage != null) return false

    val lastVisibleItemIndex =
        listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return false

    /* N - N/4 */
    val prefetchDistance = (pageSize / PrefetchDivider).coerceAtLeast(1)
    val triggerIndex = (productsCount - prefetchDistance - 1).coerceAtLeast(0)

    return lastVisibleItemIndex >= triggerIndex
}

/** карточка товара */
@Composable
private fun ProductCard(
    product: ProductPreviewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(ProductCardHeight),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                /* идентификатор */
                Text(
                    text = stringResource(
                        R.string.product_id,
                        product.id,
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
                /* название */
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                /* бренд */
                Text(
                    text = product.brand ?: stringResource(R.string.no_brand),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                /* ценник */
                Text(
                    text = product.price.toPriceText(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyContent(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )

            Button(
                onClick = onRetryClick
            ) {
                Text(text = stringResource(R.string.repeat))
            }
        }
    }
}

/** для пагинации - осуществляет подготовку количества элементов, которое
 * нужно загрузить */
@Composable
private fun rememberCalculatedPageSize(
    viewportHeight : Dp,
) : Int {
    val density = LocalDensity.current

    /* пересчитываем только при изменении viewportHeight или density */
    return remember(viewportHeight, density) {
        val viewportHeightPx = with(density) {
            viewportHeight.toPx()
        }

        val oneItemHeightPx = with(density) {
            (ProductCardHeight + ProductCardSpacing).toPx()
        }

        /* N = (высота экрана) / (высота одной карточки) * 2 */
        val visibleCardsCount = floor(
            viewportHeightPx / oneItemHeightPx
        ).toInt().coerceAtLeast(1)

        visibleCardsCount * PageSizeMultiplier
    }
}

private fun Double.toPriceText() : String {
    return String.format(Locale.US, "$%.2f", this)
}

private object ProductsListItemKeys {
    const val PAGE_LOADING = "page_loading"
    const val PAGE_ERROR = "page_error"
    const val END_REACHED = "end_reached"
}