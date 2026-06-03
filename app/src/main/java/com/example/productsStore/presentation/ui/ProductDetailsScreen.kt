package com.example.productsStore.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.productsStore.domain.model.ProductDetailsModel
import com.example.productsStore.presentation.state.ProductDetailsUiState
import com.example.productsstore.R
import java.util.Locale

/** обработчик состояний */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    state: ProductDetailsUiState,
    onAddToCartClick: () -> Unit,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.product)) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        when (state) {
            is ProductDetailsUiState.Success -> {
                ProductDetailsContent(
                    product = state.product,
                    isStale = state.isStale,
                    onAddToCartClick = onAddToCartClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            ProductDetailsUiState.Loading -> {
                LoadingContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            ProductDetailsUiState.Empty -> {
                EmptyContent(
                    text = stringResource(R.string.product_card_is_empty),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            is ProductDetailsUiState.Error -> {
                ErrorContent(
                    message = state.message,
                    onRetryClick = onRetryClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun ProductDetailsContent(
    product: ProductDetailsModel,
    isStale: Boolean,
    onAddToCartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProductImage(
                    imageUrl = product.imageUrl,
                    title = product.title,
                )

                if (isStale) {
                    StaleDataBadge()
                }

                Text(
                    text = product.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                HorizontalDivider()

                ProductInfoRow(
                    title = stringResource(R.string.price),
                    value = product.price.toPriceText()
                )

                ProductInfoRow(
                    title = stringResource(R.string.rating),
                    value = product.rating.toString(),
                )

                ProductInfoRow(
                    title = stringResource(R.string.weight),
                    value = stringResource(
                        R.string.weight_in_grams,
                        product.weight,
                    ),
                )

                ProductInfoRow(
                    title = stringResource(R.string.availability),
                    value = product.availabilityStatus,
                )

                ProductInfoRow(
                    title = stringResource(R.string.warranty),
                    value = product.warrantyInformation,
                )

                Button(
                    onClick = onAddToCartClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.add_to_cart))
                }
            }
        }
    }
}

/** шаблонная Row для размещения Название - Данные */
@Composable
private fun ProductInfoRow(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ProductImage(
    imageUrl: String?,
    title: String,
    modifier: Modifier = Modifier,
) {
    if (imageUrl == null) {
        Box(
            modifier = modifier
                .testTag(ProductDetailsTestTags.PRODUCT_IMAGE_PLACEHOLDER)
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.no_image),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    } else {
        GlideImage(
            model = imageUrl,
            contentDescription = title,
            modifier = modifier
                .testTag(ProductDetailsTestTags.PRODUCT_IMAGE)
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun StaleDataBadge(
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.data_is_stale),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        color = MaterialTheme.colorScheme.onErrorContainer,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
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
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )

            Button(
                onClick = onRetryClick,
            ) {
                Text(text = stringResource(R.string.repeat))
            }
        }
    }
}

private fun Double.toPriceText() : String {
    return String.format(Locale.US, "$%.2f", this)
}