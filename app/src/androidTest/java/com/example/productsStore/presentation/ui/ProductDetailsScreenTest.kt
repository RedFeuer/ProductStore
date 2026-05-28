package com.example.productsStore.presentation.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.productsStore.domain.model.ProductDetailsModel
import com.example.productsStore.presentation.state.ProductDetailsUiState
import com.example.productsStore.presentation.theme.ProductsStoreTheme
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

        // GIVEN

        // WHEN

        // THEN

@RunWith(AndroidJUnit4::class)
class ProductDetailsScreenTest : TestCase() {

    @get:Rule
    val composeRule = createComposeRule()

    /* тестер для проверки работы тестов */
    @Test
    fun checkUi() = run {
        ActivityScenario.launch(MainActivity::class.java)
        Thread.sleep(2000L)
    }

    @Test
    fun givenProductWithImageUrlWhenScreenDisplayedThenImageDisplayed() = run {
        // GIVEN
        val state = ProductDetailsUiState.Success(
            product = createProductDetailsModel(
                imageUrl = "https://example.com/product.png",
            ),
            isStale = false,
        )

        // WHEN
        composeRule.setContent {
            ProductsStoreTheme {
                ProductDetailsScreen(
                    state = state,
                    onAddToCartClick = {},
                    onBackClick = {},
                    onRetryClick = {},
                )
            }
        }

        // THEN
        composeRule
            .onNodeWithTag(ProductDetailsTestTags.PRODUCT_IMAGE)
            .assertIsDisplayed()

        composeRule
            .onNodeWithTag(ProductDetailsTestTags.PRODUCT_IMAGE_PLACEHOLDER)
            .assertDoesNotExist()
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