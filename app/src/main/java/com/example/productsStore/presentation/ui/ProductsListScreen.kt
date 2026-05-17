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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.productsStore.domain.model.ProductPreviewModel
import com.example.productsStore.presentation.state.ProductListUiState
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
    onAddToCartClick: (ProductPreviewModel) -> Unit,
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
                        text = "Товары",
                        fontWeight = FontWeight.Bold,
                    )
                },
                actions = {
                    Button(
                        onClick = onCartClick,
                    ) {
                        Text(text = "Корзина")
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
                        onAddToCartClick = onAddToCartClick,
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
                        text = "Список товаров пуст",
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
    onAddToCartClick: (ProductPreviewModel) -> Unit,
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
                onAddToCartClick = { onAddToCartClick(product) },
            )
        }

        if (state.isPageLoading) {
            item(key = "page_loading") {
                PageLoadingContent()
            }
        }

        if (state.pageErrorMessage != null) {
            item(key = "page_error") {
                PageErrorContent(
                    message = state.pageErrorMessage,
                    onRetryClick = onRetryNextPageClick,
                )
            }
        }

        if (state.endReached) {
            item(key = "end_reached") {
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
            Text(text = "Повторить")
        }
    }
}

@Composable
private fun EndReachedContent(
    modifier: Modifier = Modifier,
) {
    Text(
        text = "Все товары загружены",
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
    onAddToCartClick: () -> Unit,
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
                    text = "#${product.id}",
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
                /* бредн */
                Text(
                    text = product.brand ?: "Без бренда",
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

                /* кнопка добавить в корзину */
                Button(
                    onClick = onAddToCartClick,
                ) {
                    Text(text = "В корзину")
                }
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
                Text(text = "Повторить")
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