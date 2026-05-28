package com.example.productsStore.presentation.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.productsStore.domain.model.CartProductModel
import com.example.productsStore.presentation.elm.cart.CartState
import com.example.productsStore.presentation.theme.ProductsStoreTheme
import com.example.productsStore.presentation.ui.screen.CartComposeScreen
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.run

@RunWith(AndroidJUnit4::class)
class CartScreenTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport(),
) {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun givenCartWithProductsWhenScreenDisplayedThenClearCartButtonDisplayed() = run {
        step("Открываем корзину с товарами") {
            composeRule.setContent {
                ProductsStoreTheme {
                    CartScreen(
                        state = CartState.Success(
                            products = listOf(
                                createCartProductModel(
                                    productId = 1,
                                    quantity = 2,
                                )
                            )
                        ),
                        onBackClick = {},
                        onProductClick = {},
                        onClearCartClick = {},
                    )
                }
            }
        }

        step("Проверяем, что кнопка очистки корзины отображается") {
            onComposeScreen<CartComposeScreen>(composeRule) {
                clearCartButton {
                    assertIsDisplayed()
                }
            }
        }
    }

    @Test
    fun givenEmptyCartWhenScreenDisplayedThenClearCartButtonDoesNotExist() = run {
        step("Открываем пустую корзину") {
            composeRule.setContent {
                ProductsStoreTheme {
                    CartScreen(
                        state = CartState.Empty,
                        onBackClick = {},
                        onProductClick = {},
                        onClearCartClick = {},
                    )
                }
            }
        }

        step("Проверяем, что кнопка очистки корзины отсутствует") {
            onComposeScreen<CartComposeScreen>(composeRule) {
                clearCartButton {
                    assertDoesNotExist()
                }
            }
        }
    }

    private fun createCartProductModel(
        productId: Int = 1,
        title: String = "Product",
        price: Double = 10.0,
        brand: String? = "Brand",
        quantity: Int = 1,
    ) : CartProductModel {
        return CartProductModel(
            productId = productId,
            title = title,
            price = price,
            brand = brand,
            quantity = quantity,
        )
    }
}