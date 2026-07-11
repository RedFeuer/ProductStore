package com.example.productsStore.presentation.ui.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.example.productsStore.presentation.ui.ProductDetailsTestTags
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class ProductDetailsComposeScreen(
    semanticsProvider: SemanticsNodeInteractionsProvider,
) : ComposeScreen<ProductDetailsComposeScreen>(
    semanticsProvider = semanticsProvider,
) {
    val productImage: KNode = child {
        hasTestTag(ProductDetailsTestTags.PRODUCT_IMAGE)
    }

    val productImagePlaceholder: KNode = child {
        hasTestTag(ProductDetailsTestTags.PRODUCT_IMAGE_PLACEHOLDER)
    }
}