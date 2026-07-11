package com.example.productsStore.data.repositoryImpl.productDetails

import com.example.productsStore.data.local.dao.ProductDetailsDao
import com.example.productsStore.data.local.mapper.ProductDetailsEntityMapper
import com.example.productsStore.data.remote.api.ProductsApi
import com.example.productsStore.data.remote.mapper.ProductDetailsDtoMapper
import com.example.productsStore.domain.model.CachedProductDetailsModel
import com.example.productsStore.domain.provider.time.CurrentTimeProvider
import com.example.productsStore.domain.repository.productDetails.ProductDetailsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductDetailsRepositoryImpl @Inject constructor(
    private val productsApi: ProductsApi,
    private val productDetailsDao: ProductDetailsDao,
    private val productsDetailsEntityMapper: ProductDetailsEntityMapper,
    private val productsDetailsDtoMapper: ProductDetailsDtoMapper,
    private val currentTimeProvider: CurrentTimeProvider,
) : ProductDetailsRepository {
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
            * а показываем пометку "Данные устарели" у старых данных */
            if (cachedProductDetails == null) {
                throw exception
            }
        }
    }

    private companion object {
        const val HOURS_IN_DAY = 24L
        const val MINUTES_IN_HOUR = 60L
        const val SECONDS_IN_MINUTE = 60L
        const val MILLIS_IN_SECOND = 1000L

        const val PRODUCT_DETAILS_CACHE_TTL_MILLIS =
            HOURS_IN_DAY * MINUTES_IN_HOUR * SECONDS_IN_MINUTE * MILLIS_IN_SECOND
    }
}