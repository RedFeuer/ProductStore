package com.example.productsStore.data.repositoryImpl.productsList

import com.example.productsStore.data.local.dao.ProductPreviewDao
import com.example.productsStore.data.local.mapper.ProductPreviewEntityMapper
import com.example.productsStore.data.remote.api.ProductsApi
import com.example.productsStore.data.remote.mapper.ProductsPageDtoMapper
import com.example.productsStore.domain.model.ProductPreviewModel
import com.example.productsStore.domain.model.ProductsPageModel
import com.example.productsStore.domain.repository.productsList.ProductsListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductsListRepositoryImpl @Inject constructor(
    private val productsApi: ProductsApi,
    private val productsPreviewDao: ProductPreviewDao,
    private val productsPreviewEntityMapper: ProductPreviewEntityMapper,
    private val productsPageDtoMapper: ProductsPageDtoMapper
) : ProductsListRepository {
    /** подгрузка списка из БД */
    override suspend fun observeProductPreviews(limit: Int): Flow<List<ProductPreviewModel>> {
        return productsPreviewDao.observeProducts(limit)
            .map { entities ->
                entities.map { productPreviewEntity ->
                    productsPreviewEntityMapper.toDomainModel(productPreviewEntity)
                }
            }
            .distinctUntilChanged()
    }

    /** подгрузка списка из сети + сохранение в БД */
    override suspend fun refreshProductsPage(limit: Int, skip: Int): ProductsPageModel {
        val remotePageModel = productsPageDtoMapper.toDomainModel(
            productsApi.getProductsPage(
                limit = limit,
                skip = skip,
                select = PRODUCT_PREVIEW_FIELDS,
            )
        )

        productsPreviewDao.upsertProducts(
            products = remotePageModel.products.map { productPreviewModel ->
                productsPreviewEntityMapper.toEntity(productPreviewModel)
            }
        )

        return remotePageModel
    }

    private companion object {
        const val PRODUCT_PREVIEW_FIELDS = "id,title,price,brand"
    }
}