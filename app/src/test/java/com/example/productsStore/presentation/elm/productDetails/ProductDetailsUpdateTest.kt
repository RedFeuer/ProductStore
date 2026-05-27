package com.example.productsStore.presentation.elm.productDetails

import com.example.productsStore.domain.model.CachedProductDetailsModel
import com.example.productsStore.domain.model.ProductDetailsModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

        // GIVEN

        // WHEN

        // THEN

class ProductDetailsUpdateTest {

    private val update = ProductDetailsUpdate()

    @Test
    fun `GIVEN cached products loaded WHEN update THEN state is success`() {
        // GIVEN
        val initialState = ProductDetailsState(
            productId = PRODUCT_ID,
        )

        val product = createProductDetailsModel(
            id = PRODUCT_ID,
            title = "Product",
        )

        val event = ProductDetailsEvent.CachedProductDetailsLoaded(
            cachedProductDetails = CachedProductDetailsModel(
                product = product,
                isStale = false,
            )
        )

        // WHEN
        val actual = update.update(
            state = initialState,
            event = event,
        )

        // THEN
        val actualState = requireNotNull(actual.state) {
            "После CachedProductDetailsLoaded ожидалось новое состояние, но state == null"
        }

        val actualContentState = actualState.contentState as ProductDetailsContentState.Success

        assertEquals(product, actualContentState.product)
        assertEquals(false, actualContentState.isStale)
        assertTrue(actual.commands.isEmpty())
        assertTrue(actual.news.isEmpty())
    }

    @Test
    fun `GIVEN stale cached product loaded WHEN update THEN state is success with stale flag`() {
        // GIVEN
        val initialState = ProductDetailsState(
            productId = PRODUCT_ID,
        )

        val product = createProductDetailsModel(
            id = PRODUCT_ID,
            title = "Old product",
        )

        val event = ProductDetailsEvent.CachedProductDetailsLoaded(
            cachedProductDetails = CachedProductDetailsModel(
                product = product,
                isStale = true,
            )
        )

        // WHEN
        val actual = update.update(
            state = initialState,
            event = event,
        )

        // THEN
        val actualState = requireNotNull(actual.state) {
            "После CachedProductDetailsLoaded ожидалось новое состояние, но state == null"
        }
        val actualContentState = actualState.contentState as ProductDetailsContentState.Success

        assertEquals(product, actualContentState.product)
        assertEquals(true, actualContentState.isStale)
    }

    @Test
    fun `GIVEN loading state WHEN refresh failed THEN state if error`() {
        // GIVEN
        val initialState = ProductDetailsState(
            productId = PRODUCT_ID,
            contentState = ProductDetailsContentState.Loading
        )

        val event = ProductDetailsEvent.ProductDetailsRefreshingFailed(
            message = NETWORK_ERROR_MESSAGE,
        )

        // WHEN
        val actual = update.update(
            state = initialState,
            event = event,
        )

        // THEN
        val actualState = requireNotNull(actual.state) {
            "После ProductDetailsRefreshingFailed ожидалось другоео состояние, но state = nullЭ"
        }
        val actualContentState = actualState.contentState as ProductDetailsContentState.Error

        assertEquals(NETWORK_ERROR_MESSAGE, actualContentState.message)
        assertTrue(actual.commands.isEmpty())
        assertTrue(actual.news.isEmpty())
    }

    private fun createProductDetailsModel(
        id: Int = 1,
        title: String = "Product",
        price: Double = 10.0,
        brand: String? = "Brand",
        description: String = "Descriprion",
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

    private companion object {
        const val PRODUCT_ID = 1
        const val NETWORK_ERROR_MESSAGE = "Network error"
    }
}