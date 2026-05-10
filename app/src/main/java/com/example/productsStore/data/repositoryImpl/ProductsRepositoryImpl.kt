package com.example.productsStore.data.repositoryImpl

import com.example.productsStore.data.remote.api.ProductsApi
import com.example.productsStore.data.remote.mapper.ProductDetailsMapper
import com.example.productsStore.data.remote.mapper.ProductsPageMapper
import com.example.productsStore.domain.model.ProductDetailsModel
import com.example.productsStore.domain.model.ProductsPageModel
import com.example.productsStore.domain.repository.ProductsRepository
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor (
    private val productsApi: ProductsApi,
    private val productsPageMapper: ProductsPageMapper,
    private val productsDetailsMapper: ProductDetailsMapper,
) : ProductsRepository {
    override suspend fun getProductsPage(limit: Int, skip: Int): ProductsPageModel {
        return productsPageMapper.toDomainModel(
            productsApi.getProductsPage(
                limit = limit,
                skip = skip,
                select = PRODUCT_PREVIEW_FIELDS,
            )
        )
    }

    override suspend fun getProductsDetails(id: Int): ProductDetailsModel {
        return productsDetailsMapper.toDomainModel(
            productsApi.getProductDetails(id)
        )
    }

    private companion object {
        const val PRODUCT_PREVIEW_FIELDS = "id,title,price,brand"
    }
}