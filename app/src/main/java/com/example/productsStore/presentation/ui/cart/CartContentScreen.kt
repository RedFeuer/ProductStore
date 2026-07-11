package com.example.productsStore.presentation.ui.cart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.productsStore.domain.model.CartProductModel
import com.example.productsStore.presentation.elm.cart.CartIntent
import com.example.productsStore.presentation.ui.CartTestTags
import com.example.productsstore.R
import java.util.Locale

@Composable
fun CartContent(
    products: List<CartProductModel>,
    onIntent: (CartIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = products,
            key = { product -> product.productId },
        ) { product ->
            CartProductCard(
                product = product,
                onIntent = onIntent,
            )
        }
    }
}

@Composable
private fun CartProductCard(
    product: CartProductModel,
    onIntent: (CartIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        onClick = {
            onIntent(
                CartIntent.ProductClicked(
                    productId = product.productId,
                )
            )
        },
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        )
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
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )

                Text(
                    text = product.brand ?: stringResource(R.string.no_brand),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = stringResource(
                        R.string.cart_product_quantity,
                        product.quantity,
                    ),
                    modifier = Modifier.testTag(
                        CartTestTags.CART_PRODUCT_QUANTITY_PREFIX + product.productId
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Checkbox(
                        checked = product.reminderEnabled,
                        onCheckedChange = { enabled ->
                            onIntent(
                                CartIntent.ReminderCheckedChanged(
                                    productId = product.productId,
                                    enabled = enabled,
                                )
                            )
                        }
                    )

                    Text(
                        text = stringResource(R.string.remind_about_purchase),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Text(
                text = product.price.toPriceText(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private fun Double.toPriceText(): String {
    return String.format(Locale.US, "$%.2f", this)
}