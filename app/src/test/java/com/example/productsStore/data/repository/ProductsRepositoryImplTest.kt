package com.example.productsStore.data.repository

import com.example.productsStore.data.local.dao.CartProductDao
import com.example.productsStore.data.local.dao.ProductDetailsDao
import com.example.productsStore.data.local.dao.ProductPreviewDao
import com.example.productsStore.data.local.entity.CartProductEntity
import com.example.productsStore.data.local.entity.ProductDetailsEntity
import com.example.productsStore.data.local.entity.ProductPreviewEntity
import com.example.productsStore.data.local.mapper.CartProductEntityMapper
import com.example.productsStore.data.local.mapper.ProductDetailsEntityMapper
import com.example.productsStore.data.local.mapper.ProductPreviewEntityMapper
import com.example.productsStore.data.remote.api.ProductsApi
import com.example.productsStore.data.remote.dto.ProductDetailsDto
import com.example.productsStore.data.remote.dto.ProductsPageDto
import com.example.productsStore.data.remote.mapper.ProductDetailsDtoMapper
import com.example.productsStore.data.remote.mapper.ProductPreviewDtoMapper
import com.example.productsStore.data.remote.mapper.ProductsPageDtoMapper
import com.example.productsStore.data.repositoryImpl.ProductsRepositoryImpl
import com.example.productsStore.domain.provider.time.CurrentTimeProvider
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.fail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.junit.Test

class ProductsRepositoryImplTest {
    @Test
    fun `GIVEN fresh cache WHEN refresh product details THEN do not request network`() = runTest {
        // GIVEN
        val currentTimeMillis = 10_000L
        val cachedDetails = createProductDetailsEntity(
            loadedAtMillis = currentTimeMillis - ONE_HOUR_MILLIS,
        )
        val productsApi = FakeProductsApi()
        val productDetailsDao = FakeProductDetailsDao(
            initialProductDetails = cachedDetails,
        )

        val repository = createRepository(
            productsApi = productsApi,
            productDetailsDao = productDetailsDao,
            currentTimeMillis = currentTimeMillis,
        )

        // WHEN
        repository.refreshProductDetailsIfNeeded(id = PRODUCT_ID)

        // THEN
        assertEquals(0, productsApi.getProductDetailsCallsCount)
        assertEquals(cachedDetails, productDetailsDao.savedProductDetails)
    }

    @Test
    fun `GIVEN stale cache WHEN refresh product details THEN request network and update cache`() = runTest {
        // GIVEN
        val currentTimeMillis = 100_000_000L
        val cachedDetails = createProductDetailsEntity(
            title = "Old title",
            loadedAtMillis = currentTimeMillis - CACHE_TTL_MILLIS - 1L,
        )

        val remoteDetails = createProductDetailsDto(
            title = "New title",
        )

        val productsApi = FakeProductsApi(
            productsDetailsResponse = remoteDetails,
        )

        val productDetailsDao = FakeProductDetailsDao(
            initialProductDetails = cachedDetails,
        )

        val repository = createRepository(
            productsApi = productsApi,
            productDetailsDao = productDetailsDao,
            currentTimeMillis = currentTimeMillis,
        )

        // WHEN
        repository.refreshProductDetailsIfNeeded(PRODUCT_ID)

        // THEN
        assertEquals(1, productsApi.getProductDetailsCallsCount)
        assertEquals("New title", productDetailsDao.savedProductDetails?.title)
        assertEquals(currentTimeMillis, productDetailsDao.savedProductDetails?.loadedAtMillis)
    }

    @Test
    fun `GIVEN empty cache WHEN refresh product details THEN request network and save data`() = runTest {
        // GIVEN
        val currentTimeMillis = 200_000L

        val productsApi = FakeProductsApi(
            productsDetailsResponse = createProductDetailsDto(
                title = "Loaded from network"
            )
        )

        val productDetailsDao = FakeProductDetailsDao(
            initialProductDetails = null,
        )

        val repository = createRepository(
            productsApi = productsApi,
            productDetailsDao = productDetailsDao,
            currentTimeMillis = currentTimeMillis,
        )

        // WHEN
        repository.refreshProductDetailsIfNeeded(id = PRODUCT_ID)

        // THEN
        assertEquals(1, productsApi.getProductDetailsCallsCount)
        assertEquals("Loaded from network", productDetailsDao.savedProductDetails?.title)
        assertEquals(currentTimeMillis, productDetailsDao.savedProductDetails?.loadedAtMillis)
    }

    @Test
    fun `GIVEN stale cache and network error WHEN refresh product details THEN keep cached data`() = runTest {
        // GIVEN
        val currentTimeMillis = 100_000_000L

        val cachedDetails = createProductDetailsEntity(
            title = "Old cached title",
            loadedAtMillis = currentTimeMillis - CACHE_TTL_MILLIS - 1L,
        )

        val productsApi = FakeProductsApi(
            productsDetailsException = IOException("Network Error"),
        )

        val productDetailsDao = FakeProductDetailsDao(
            initialProductDetails = cachedDetails,
        )

        val repository = createRepository(
            productsApi = productsApi,
            productDetailsDao = productDetailsDao,
            currentTimeMillis = currentTimeMillis,
        )

        // WHEN
        repository.refreshProductDetailsIfNeeded(id = PRODUCT_ID)

        // THEN
        assertEquals(1, productsApi.getProductDetailsCallsCount)
        assertEquals(cachedDetails, productDetailsDao.savedProductDetails)
    }

    @Test
    fun `GIVEN empty cache and network error WHEN refresh product details THEN throw exception`() = runTest {
        // GIVEN
        val expectedException = "Network error"

        val currentTimeMillis = 100_000L

        val productsApi = FakeProductsApi(
            productsDetailsException = IOException(expectedException),
        )

        val productDetailsDao = FakeProductDetailsDao(
            initialProductDetails = null,
        )

        val repository = createRepository(
            productsApi = productsApi,
            productDetailsDao = productDetailsDao,
            currentTimeMillis = currentTimeMillis,
        )

        // WHEN
        try {
            repository.refreshProductDetailsIfNeeded(id = PRODUCT_ID)
            fail("Не выброшена ожидаемая ошибка: $expectedException")
        } catch (exception: IOException) {
            // THEN
            assertEquals(expectedException, exception.message)
        }

        // THEN
        assertEquals(1, productsApi.getProductDetailsCallsCount)
        assertNull(productDetailsDao.savedProductDetails)
    }

    @Test
    fun `GIVEN successful network response WHEN refresh empty product details THEN save data with current timestamp`() = runTest {
        // GIVEN
        val currentTimeMillis = 555_000L

        val productsApi = FakeProductsApi(
            productsDetailsResponse = createProductDetailsDto(
                title = "New product",
            )
        )

        val productDetailsDao = FakeProductDetailsDao(
            initialProductDetails = null,
        )

        val repository = createRepository(
            productsApi = productsApi,
            productDetailsDao = productDetailsDao,
            currentTimeMillis = currentTimeMillis,
        )

        // WHEN
        repository.refreshProductDetailsIfNeeded(id = PRODUCT_ID)

        // THEN
        assertEquals(1, productsApi.getProductDetailsCallsCount)
        assertEquals(currentTimeMillis, productDetailsDao.savedProductDetails?.loadedAtMillis)
    }

    @Test
    fun `GIVEN successful network response WHEN refresh existing product details THEN save data with current timestamp`() = runTest {
        // GIVEN
        val currentTimeMillis = 100_000_000L

        val productsApi = FakeProductsApi(
            productsDetailsResponse = createProductDetailsDto(
                title = "New product",
            )
        )

        val cachedDetails = createProductDetailsEntity(
            title = "Old product",
            loadedAtMillis = currentTimeMillis - CACHE_TTL_MILLIS - 1L,
        )

        val productDetailsDao = FakeProductDetailsDao(
            initialProductDetails = cachedDetails,
        )

        val repository = createRepository(
            productsApi = productsApi,
            productDetailsDao = productDetailsDao,
            currentTimeMillis = currentTimeMillis,
        )

        // WHEN
        repository.refreshProductDetailsIfNeeded(id = PRODUCT_ID)

        // THEN
        assertEquals(1, productsApi.getProductDetailsCallsCount)
        assertEquals("New product", productDetailsDao.savedProductDetails?.title)
        assertEquals(currentTimeMillis, productDetailsDao.savedProductDetails?.loadedAtMillis)
    }

    private fun createRepository(
        productsApi: ProductsApi = FakeProductsApi(),
        productsPreviewDao: ProductPreviewDao = FakeProductPreviewDao(),
        productDetailsDao: ProductDetailsDao = FakeProductDetailsDao(),
        cartProductDao: CartProductDao = FakeCartDao(),
        currentTimeMillis: Long = 0L,
    ) : ProductsRepositoryImpl {
        val productPreviewDtoMapper = ProductPreviewDtoMapper()
        val productDetailsDtoMapper = ProductDetailsDtoMapper()
        val productsPageDtoMapper = ProductsPageDtoMapper(
            productPreviewDtoMapper = productPreviewDtoMapper,
        )
        val productPreviewEntityMapper = ProductPreviewEntityMapper()
        val productDetailsEntityMapper = ProductDetailsEntityMapper()
        val cartProductEntityMapper = CartProductEntityMapper()

        return ProductsRepositoryImpl(
            productsApi = productsApi,
            productsPreviewDao = productsPreviewDao,
            productDetailsDao = productDetailsDao,
            cartProductDao = cartProductDao,
            productsDetailsDtoMapper = productDetailsDtoMapper,
            productsPageDtoMapper = productsPageDtoMapper,
            productsPreviewEntityMapper = productPreviewEntityMapper,
            productsDetailsEntityMapper = productDetailsEntityMapper,
            cartProductEntityMapper = cartProductEntityMapper,
            currentTimeProvider = FakeCurrentTimeProvider(
                currentTimeMillis = currentTimeMillis,
            ),
        )
    }

    private class FakeCurrentTimeProvider(
        private val currentTimeMillis: Long,
    ) : CurrentTimeProvider {
        override fun currentTimeMillis(): Long {
            return currentTimeMillis
        }

    }

    private class FakeProductsApi(
        private val productsDetailsResponse: ProductDetailsDto = createProductDetailsDto(),
        private val productsDetailsException: Exception? = null,
    ) : ProductsApi {
        var getProductDetailsCallsCount : Int = 0
            private set

        override suspend fun getProductsPage(
            limit: Int,
            skip: Int,
            select: String
        ): ProductsPageDto {
            return ProductsPageDto(
                products = emptyList(),
                total = 0,
                skip = skip,
                limit = limit,
            )
        }

        override suspend fun getProductDetails(id: Int): ProductDetailsDto {
            ++getProductDetailsCallsCount

            val exception = productsDetailsException
            if (exception != null) {
                throw exception
            }

            return productsDetailsResponse
        }

    }

    private class FakeProductDetailsDao(
        initialProductDetails: ProductDetailsEntity? = null,
    ) : ProductDetailsDao {
        private val productDetailsFlow = MutableStateFlow(initialProductDetails)

        var savedProductDetails: ProductDetailsEntity? = initialProductDetails
            private set

        override suspend fun upsertProductDetails(product: ProductDetailsEntity) {
            savedProductDetails = product
            productDetailsFlow.value = product
        }

        override fun observeProductDetailsById(id: Int): Flow<ProductDetailsEntity?> {
            return productDetailsFlow
        }

        override suspend fun getProductDetailsById(id: Int): ProductDetailsEntity? {
            return savedProductDetails?.takeIf { productDetailsEntity ->
                productDetailsEntity.id == id
            }
        }

    }

    private class FakeProductPreviewDao : ProductPreviewDao {
        private val productsFlow = MutableStateFlow<List<ProductPreviewEntity>>(emptyList())

        override suspend fun upsertProducts(products: List<ProductPreviewEntity>) {
            productsFlow.value = products
        }

        override fun observeProducts(limit: Int): Flow<List<ProductPreviewEntity>> {
            return productsFlow.map { products ->
                products.take(limit)
            }
        }

        override suspend fun getProductsCount(): Int {
            return productsFlow.value.size
        }

        override suspend fun clearProducts() {
            productsFlow.value = emptyList()
        }
    }

    private class FakeCartDao : CartProductDao() {
        private val cartProductsFlow = MutableStateFlow<List<CartProductEntity>>(emptyList())

        override suspend fun getProductsWithEnabledReminders(): List<CartProductEntity> {
            return cartProductsFlow.value.filter { cartProductsEntity ->
                cartProductsEntity.reminderEnabled
            }
        }

        override suspend fun updateReminderEnabled(productId: Int, enabled: Boolean) {
            cartProductsFlow.value = cartProductsFlow.value.map { cartProductEntity ->
                if (cartProductEntity.productId == productId) {
                    cartProductEntity.copy(reminderEnabled = enabled)
                } else {
                    cartProductEntity
                }
            }
        }

        override fun observeCartProducts(): Flow<List<CartProductEntity>> {
            return cartProductsFlow
        }

        override suspend fun clearCart() {
            cartProductsFlow.value = emptyList()
        }

        override suspend fun getQuantityByProductId(productId: Int): Int? {
            return cartProductsFlow.value
                .firstOrNull { cartProductEntity -> cartProductEntity.productId == productId }
                ?.quantity
        }

        override suspend fun insertCartProduct(cartProduct: CartProductEntity) {
            if (getQuantityByProductId(cartProduct.productId) == null) {
                cartProductsFlow.value += cartProduct
            }
        }

        override suspend fun updateQuantity(productId: Int, quantity: Int) {
            cartProductsFlow.value = cartProductsFlow.value.map { cartProductEntity ->
                if (cartProductEntity.productId == productId) {
                    cartProductEntity.copy(quantity = quantity)
                } else {
                    cartProductEntity
                }
            }
        }

    }

    private companion object {
        const val PRODUCT_ID = 1

        const val ONE_HOUR_MILLIS = 60L * 60L * 1000L
        const val CACHE_TTL_MILLIS = 24L * 60L * 60L * 1000L
    }
}

private fun createProductDetailsDto(
    id: Int = 1,
    title: String = "Essence Mascara Lash Princess",
    price: Double = 9.99,
    brand: String? = "Essence",
    description: String = "Popular mascara",
    rating: Double = 4.94,
    weight: Int = 2,
    availabilityStatus: String = "In Stock",
    warrantyInformation: String = "1 week warranty",
    thumbnail: String? = "https://example.com/thumbnail.png",
    images: List<String> = listOf("https://example.com/image.png"),
): ProductDetailsDto {
    return ProductDetailsDto(
        id = id,
        title = title,
        price = price,
        brand = brand,
        description = description,
        rating = rating,
        weight = weight,
        availabilityStatus = availabilityStatus,
        warrantyInformation = warrantyInformation,
        thumbnail = thumbnail,
        images = images,
    )
}

private fun createProductDetailsEntity(
    id: Int = 1,
    title: String = "Cached product",
    price: Double = 9.99,
    brand: String? = "Cached brand",
    description: String = "Cached description",
    rating: Double = 4.5,
    weight: Int = 2,
    availabilityStatus: String = "In Stock",
    warrantyInformation: String = "Cached warranty",
    imageUrl: String? = "https://example.com/cached.png",
    loadedAtMillis: Long = 0L,
): ProductDetailsEntity {
    return ProductDetailsEntity(
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
        loadedAtMillis = loadedAtMillis,
    )
}