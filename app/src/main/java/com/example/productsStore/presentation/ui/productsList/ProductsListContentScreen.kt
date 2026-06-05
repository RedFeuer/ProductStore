package com.example.productsStore.presentation.ui.productsList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.productsStore.domain.model.ProductPreviewModel
import com.example.productsStore.presentation.elm.productsList.ProductsListIntent
import com.example.productsStore.presentation.state.ProductListUiState
import com.example.productsstore.R
import java.util.Locale

object CardSize {
    val ProductCardHeight = 120.dp
    val ProductCardSpacing = 12.dp
}

/** список товаров */
@Composable
fun ProductsListContent(
    state: ProductListUiState.Success,
    pageSize: Int,
    onIntent: (ProductsListIntent) -> Unit,
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
            onIntent(ProductsListIntent.LoadNextPage)
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
                onClick = {
                    onIntent(
                        ProductsListIntent.ProductClicked(
                            productId = product.id,
                        )
                    )
                },
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
                    onIntent = onIntent,
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

/** для пагинации - осуществляет подготовку количества элементов, которое
 * нужно загрузить */
@Composable
fun rememberCalculatedPageSize(
    viewportHeightPx : Int,
) : Int {
    val density = LocalDensity.current

    /* пересчитываем только при изменении viewportHeight или density */
    return remember(viewportHeightPx, density) {
        if (viewportHeightPx <= 0) {
            return@remember 0
        }

        val oneItemHeightPx = with(density) {
            (CardSize.ProductCardHeight + CardSize.ProductCardSpacing).roundToPx()
        }

        /* N = (высота экрана) / (высота одной карточки) * 2 */
        val visibleCardsCount = (
            viewportHeightPx / oneItemHeightPx
        ).coerceAtLeast(1)

        visibleCardsCount * PaginationConstants.PAGE_SIZE_MULTIPLIER
    }
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
    val prefetchDistance = (pageSize / PaginationConstants.PREFETCH_DIVIDER).coerceAtLeast(1)
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
            .height(CardSize.ProductCardHeight),
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

private fun Double.toPriceText() : String {
    return String.format(Locale.US, "$%.2f", this)
}

private object ProductsListItemKeys {
    const val PAGE_LOADING = "page_loading"
    const val PAGE_ERROR = "page_error"
    const val END_REACHED = "end_reached"
}

private object PaginationConstants {
    const val PAGE_SIZE_MULTIPLIER = 2
    const val PREFETCH_DIVIDER = 4
}