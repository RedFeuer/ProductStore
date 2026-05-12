package com.example.productsStore.data.repositoryImpl

import com.example.productsStore.data.local.dao.ProductDetailsDao
import com.example.productsStore.data.local.dao.ProductPreviewDao
import com.example.productsStore.data.local.mapper.ProductDetailsEntityMapper
import com.example.productsStore.data.local.mapper.ProductPreviewEntityMapper
import com.example.productsStore.data.remote.api.ProductsApi
import com.example.productsStore.data.remote.mapper.ProductDetailsDtoMapper
import com.example.productsStore.data.remote.mapper.ProductsPageDtoMapper
import com.example.productsStore.domain.model.ProductDetailsModel
import com.example.productsStore.domain.model.ProductPreviewModel
import com.example.productsStore.domain.model.ProductsPageModel
import com.example.productsStore.domain.repository.ProductsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor (
    private val productsPreviewDao: ProductPreviewDao,
    private val productDetailsDao: ProductDetailsDao,
    private val productsApi: ProductsApi,
    private val productPreviewEntityMapper: ProductPreviewEntityMapper,
    private val productDetailsEntityMapper: ProductDetailsEntityMapper,
    private val productsPageDtoMapper: ProductsPageDtoMapper,
    private val productsDetailsMapper: ProductDetailsDtoMapper,
) : ProductsRepository {
    /** подгрузка списка из БД */
    override suspend fun observeProductPreviews(limit: Int): Flow<List<ProductPreviewModel>> {
        return productsPreviewDao.observeProducts(limit)
            .map { entities ->
                entities.map { productPreviewEntity ->
                    productPreviewEntityMapper.toDomainModel(productPreviewEntity) }
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
                productPreviewEntityMapper.toEntity(productPreviewModel)
            }
        )

        return remotePageModel
    }

    /** подгрузка товара из БД */
    override suspend fun observeProductDetails(id: Int): Flow<ProductDetailsModel?> {
        return productDetailsDao.observeProductDetailsById(id)
            .map { productDetailsEntity ->
                if (productDetailsEntity != null) {
                    productDetailsEntityMapper.toDomainModel(productDetailsEntity)
                } else {
                    null
                }
            }
            .distinctUntilChanged()
    }

    /** подгрузка товара из сети + сохранение в БД */
    override suspend fun refreshProductDetails(id: Int): ProductDetailsModel {
        val productDetailsModel = productsDetailsMapper.toDomainModel(
            productsApi.getProductDetails(id)
        )

        productDetailsDao.upsertProductDetails(
            product = productDetailsEntityMapper.toEntity(productDetailsModel)
        )

        return productDetailsModel
    }

    private companion object {
        const val PRODUCT_PREVIEW_FIELDS = "id,title,price,brand"
    }
}