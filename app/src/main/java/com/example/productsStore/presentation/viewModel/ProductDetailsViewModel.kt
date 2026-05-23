package com.example.productsStore.presentation.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productsStore.presentation.elm.productDetails.ProductDetailsEvent
import com.example.productsStore.presentation.elm.productDetails.ProductDetailsIntent
import com.example.productsStore.presentation.elm.productDetails.ProductDetailsNews
import com.example.productsStore.presentation.elm.productDetails.ProductDetailsState
import com.example.productsStore.presentation.elm.productDetails.ProductDetailsStoreFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.plus
import ru.tinkoff.kotea.core.Store
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    productDetailsStoreFactory: ProductDetailsStoreFactory,
) : ViewModel() {
    private val productId: Int = checkNotNull(
        savedStateHandle.get<Int>(PRODUCT_ID_ARGUMENT)
    ) {
        "ID продукта не найден"
    }

    private val store: Store<ProductDetailsState, ProductDetailsEvent, ProductDetailsNews> =
        productDetailsStoreFactory.create(
            productId = productId,
        )

    val state: StateFlow<ProductDetailsState> = store.state

    val news: Flow<ProductDetailsNews> = store.news

    init {
        store.launchIn(
            coroutineScope = viewModelScope + Dispatchers.Unconfined
        )
    }

    fun acceptIntent(intent: ProductDetailsIntent) {
        store.dispatch(ProductDetailsEvent.UserIntent(intent))
    }

    private companion object {
        const val PRODUCT_ID_ARGUMENT = "productId"
    }
}