package com.example.productsStore.domain.repository

import com.example.productsStore.domain.model.CachedProductDetailsModel
import com.example.productsStore.domain.model.CartProductModel
import com.example.productsStore.domain.model.ProductPreviewModel
import com.example.productsStore.domain.model.ProductsPageModel
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    suspend fun observeProductPreviews(limit: Int) : Flow<List<ProductPreviewModel>>
    suspend fun refreshProductsPage(limit: Int, skip: Int) : ProductsPageModel

    suspend fun observeProductDetails(id: Int) : Flow<CachedProductDetailsModel?>
    suspend fun refreshProductDetailsIfNeeded(id : Int)

    suspend fun observeCartProducts() : Flow<List<CartProductModel>>

    /** TODO: подумать, если потребуется добавлять в корзину через экран
     *  конкретного товара, то надо будет маппить Details -> Preview и добавлять */
    suspend fun addProductToCart(product: ProductPreviewModel)

    suspend fun clearCart()
}