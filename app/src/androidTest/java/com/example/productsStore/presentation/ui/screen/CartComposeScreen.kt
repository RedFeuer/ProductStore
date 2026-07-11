package com.example.productsStore.presentation.ui.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.example.productsStore.presentation.ui.CartTestTags
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class CartComposeScreen(
    semanticsProvider: SemanticsNodeInteractionsProvider,
) : ComposeScreen<CartComposeScreen>(
    semanticsProvider = semanticsProvider,
) {
    val clearCartButton: KNode = child {
        hasTestTag(CartTestTags.CLEAR_CART_BUTTON)
    }

    fun cartProductQuantity(productId: Int, function: () -> Unit): KNode {
        return child {
            hasTestTag(CartTestTags.CART_PRODUCT_QUANTITY_PREFIX + productId)
        }
    }
}