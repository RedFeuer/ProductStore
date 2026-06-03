package com.example.productsStore.data.repositoryImpl

import com.example.productsStore.data.local.dao.CartProductDao
import com.example.productsStore.data.local.dao.ProductDetailsDao
import com.example.productsStore.data.local.dao.ProductPreviewDao
import com.example.productsStore.data.local.mapper.CartProductEntityMapper
import com.example.productsStore.data.local.mapper.ProductDetailsEntityMapper
import com.example.productsStore.data.local.mapper.ProductPreviewEntityMapper
import com.example.productsStore.data.remote.api.ProductsApi
import com.example.productsStore.data.remote.mapper.ProductDetailsDtoMapper
import com.example.productsStore.data.remote.mapper.ProductsPageDtoMapper
import com.example.productsStore.domain.model.CachedProductDetailsModel
import com.example.productsStore.domain.model.CartProductModel
import com.example.productsStore.domain.model.ProductDetailsModel
import com.example.productsStore.domain.model.ProductPreviewModel
import com.example.productsStore.domain.model.ProductsPageModel
import com.example.productsStore.domain.provider.time.CurrentTimeProvider
import com.example.productsStore.domain.repository.ProductsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor (
    private val productsPreviewDao: ProductPreviewDao,
    private val productDetailsDao: ProductDetailsDao,
    private val cartProductDao: CartProductDao,
    private val productsApi: ProductsApi,
    private val productsPreviewEntityMapper: ProductPreviewEntityMapper,
    private val productsDetailsEntityMapper: ProductDetailsEntityMapper,
    private val cartProductEntityMapper: CartProductEntityMapper,
    private val productsPageDtoMapper: ProductsPageDtoMapper,
    private val productsDetailsDtoMapper: ProductDetailsDtoMapper,
    private val currentTimeProvider: CurrentTimeProvider,
) : ProductsRepository {
    /** достаем товары с активным напоминанем */
    override suspend fun getProductsWithEnabledReminders(): List<CartProductModel> {
        return cartProductDao.getProductsWithEnabledReminders()
            .map { entity ->
                cartProductEntityMapper.toDomainModel(entity)
            }
    }

    /** обновление поля установки напоминания */
    override suspend fun setProductReminderEnabled(productId: Int, enabled: Boolean) {
        cartProductDao.updateReminderEnabled(
            productId = productId,
            enabled = enabled,
        )
    }

    /** подгрузка товара из БД */
    override fun observeProductDetails(id: Int): Flow<CachedProductDetailsModel?> {
        return productDetailsDao.observeProductDetailsById(id)
            .map { productDetailsEntity ->
                if (productDetailsEntity != null) {
                    productsDetailsEntityMapper.toCachedDomainModel(
                        detailsEntity = productDetailsEntity,
                        currentTimeMillis = currentTimeProvider.currentTimeMillis(),
                        cacheTtlMillis = PRODUCT_DETAILS_CACHE_TTL_MILLIS,
                    )
                } else {
                    null
                }
            }
            .distinctUntilChanged()
    }

    /** берем данные из БД, проверяем не устарели ли
     * если данные устарели - идем в сети и подгружаем в БД новые данные
     * если данные не устарели - будем брать из БД*/
    override suspend fun refreshProductDetailsIfNeeded(id: Int) {
        val cachedProductDetails = productDetailsDao.getProductDetailsById(id)
        val currentTimeMillis = currentTimeProvider.currentTimeMillis()

        val isCacheFresh = (cachedProductDetails != null &&
                currentTimeMillis - cachedProductDetails.loadedAtMillis < PRODUCT_DETAILS_CACHE_TTL_MILLIS)

        if (isCacheFresh) return

        try {
            val remoteProductDetails = productsDetailsDtoMapper.toDomainModel(
                detailsDto = productsApi.getProductDetails(id)
            )

            productDetailsDao.upsertProductDetails(
                product = productsDetailsEntityMapper.toEntity(
                    detailsModel = remoteProductDetails,
                    loadedAtMillis = currentTimeMillis,
                )
            )
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            /* если кэш прокис, но старые закэшированные данные есть - ошибку не пробрасываем,
            * а показываем пометку "Данные устерали" у старых данных */
            if (cachedProductDetails == null) {
                throw exception
            }
        }
    }

    override fun observeCartProducts(): Flow<List<CartProductModel>> {
        return cartProductDao.observeCartProducts()
            .map { entities ->
                entities.map { cartProductEntity -> cartProductEntityMapper.toDomainModel(cartProductEntity) }
            }
            .distinctUntilChanged()
    }

    override suspend fun addProductToCart(product: ProductDetailsModel) {
        cartProductDao.addProductToCart(
            productId = product.id,
            title = product.title,
            price = product.price,
            brand = product.brand,
        )
    }

    override suspend fun clearCart() {
        cartProductDao.clearCart()
    }

    private companion object {
        const val PRODUCT_PREVIEW_FIELDS = "id,title,price,brand"

        const val HOURS_IN_DAY = 24L
        const val MINUTES_IN_HOUR = 60L
        const val SECONDS_IN_MINUTE = 60L
        const val MILLIS_IN_SECOND = 1000L

        const val PRODUCT_DETAILS_CACHE_TTL_MILLIS =
            HOURS_IN_DAY * MINUTES_IN_HOUR * SECONDS_IN_MINUTE * MILLIS_IN_SECOND
    }
}