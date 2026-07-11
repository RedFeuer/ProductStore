package com.example.productsStore.presentation.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.productsStore.domain.model.ProductDetailsModel
import com.example.productsStore.presentation.state.ProductDetailsUiState
import com.example.productsStore.presentation.theme.ProductsStoreTheme
import com.example.productsStore.presentation.ui.productDetails.ProductDetailsScreen
import com.example.productsStore.presentation.ui.screen.ProductDetailsComposeScreen
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductDetailsScreenTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport(),
) {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    /* тестер для проверки работы тестов */
    @Test
    fun checkUi() = run {
        ActivityScenario.launch(MainActivity::class.java)
        Thread.sleep(2000L)
    }

    @Test
    fun givenProductWithImageUrlWhenScreenDisplayedThenImageDisplayed() = run {
        step("Открываем экран товара с URL изображения") {
            composeRule.setContent {
                ProductsStoreTheme {
                    ProductDetailsScreen(
                        state = ProductDetailsUiState.Success(
                            product = createProductDetailsModel(
                                imageUrl = "https://example.com/product.png",
                            ),
                            isStale = false,
                        ),
                        onAddToCartClick = {},
                        onBackClick = {},
                        onRetryClick = {},
                    )
                }
            }
        }

        step("Проверяем, что изображение отображается, а заглушка отсутствует") {
            onComposeScreen<ProductDetailsComposeScreen>(composeRule) {
                productImage {
                    assertIsDisplayed()
                }

                productImagePlaceholder {
                    assertDoesNotExist()
                }
            }
        }
    }

    @Test
    fun givenProductWithoutImageUrlWhenScreenDisplayedThenImageNotDisplayed() = run {
        step("Открываем экран товара без URL изображения") {
            composeRule.setContent {
                ProductsStoreTheme {
                    ProductDetailsScreen(
                        state = ProductDetailsUiState.Success(
                            product = createProductDetailsModel(
                                imageUrl = null,
                            ),
                            isStale = false,
                        ),
                        onAddToCartClick = {},
                        onBackClick = {},
                        onRetryClick = {},
                    )
                }
            }
        }

        step("Проверяем, что изображение отсутствует, а заглушка отображается") {
            onComposeScreen<ProductDetailsComposeScreen>(composeRule) {
                productImage {
                    assertDoesNotExist()
                }

                productImagePlaceholder {
                    assertIsDisplayed()
                }
            }
        }
    }

    private fun createProductDetailsModel(
        id: Int = 1,
        title: String = "Product",
        price: Double = 10.0,
        brand: String? = "Brand",
        description: String = "Description",
        rating: Double = 4.5,
        weight: Int = 2,
        availabilityStatus: String = "In stock",
        warrantyInformation: String = "Warranty",
        imageUrl: String? = "https://example.com/image.png",
    ) : ProductDetailsModel {
        return ProductDetailsModel(
            id = id,
            title = title,
            price = price,
            brand = brand,
            description = description,
            rating = rating,
            weight = weight,
            availabilityStatus = availabilityStatus,
            warrantyInformation = warrantyInformation,
            imageUrl = imageUrl,
        )
    }
}