package com.example.productsStore.presentation.elm.productsList

import com.example.productsStore.domain.model.ProductPreviewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

        // GIVEN

        // WHEN

        // THEN

class ProductsListUpdateTest {
    private val update = ProductsListUpdate()

    @Test
    fun `GIVEN cached products loaded WHEN update THEN state contains products`() {
        // GIVEN
        val initialState = ProductsListState(
            pageSize = PAGE_SIZE,
            loadedLimit = PAGE_SIZE,
        )

        val products = listOf(
            createProductPreviewModel(id = 1),
            createProductPreviewModel(id = 2),
        )

        val event = ProductsListEvent.CachedProductsLoaded(
            products = products
        )

        // WHEN
        val actual = update.update(
            state = initialState,
            event = event,
        )

        // THEN
        val actualState = requireNotNull(actual.state) {
            "После CachedProductsLoaded state = null"
        }

        assertEquals(products, actualState.products)
        assertEquals(false, actualState.isInitialLoading)
        assertEquals(null, actualState.errorMessage)
        assertEquals(2, actualState.nextSkip)
        assertTrue(actual.commands.isEmpty())
        assertTrue(actual.news.isEmpty())
    }

    @Test
    fun `GIVEN page size calculated WHEN update THEN observe cache and refresh page commands are generated`() {
        // GIVEN
        val initialState = ProductsListState()

        val event = ProductsListEvent.UserIntent(
            intent = ProductsListIntent.PageSizeCalculated(
                pageSize = PAGE_SIZE,
            )
        )

        // WHEN
        val actual = update.update(
            state = initialState,
            event = event,
        )

        // THEN
        val actualState = requireNotNull(actual.state) {
            "После PageSizeCalculated state = null"
        }

        assertEquals(PAGE_SIZE, actualState.pageSize)
        assertEquals(PAGE_SIZE, actualState.loadedLimit)
        assertEquals(0, actualState.nextSkip)
        assertEquals(2, actual.commands.size)

        assertEquals(
            ProductsListCommand.ObserveCachedProducts(
                limit = PAGE_SIZE,
            ),
            actual.commands[0],
        )

        assertEquals(
            ProductsListCommand.RefreshPage(
                limit = PAGE_SIZE,
                skip = 0,
                isInitialLoading = true,
            ),
            actual.commands[1],
        )
    }

    private fun createProductPreviewModel(
        id: Int = 1,
        title: String = "Product",
        price: Double = 10.0,
        brand: String? = "Brand",
    ) : ProductPreviewModel {
        return ProductPreviewModel(
            id = id,
            title = title,
            price = price,
            brand = brand,
        )
    }

    private companion object {
        const val PAGE_SIZE = 20
        const val NETWORK_ERROR_MESSAGE = "Network error"
    }
}