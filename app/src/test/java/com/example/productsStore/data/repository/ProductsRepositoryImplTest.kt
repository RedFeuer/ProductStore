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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class ProductsRepositoryImplTest {

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
                skip = 0,
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