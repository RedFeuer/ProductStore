package com.example.productsStore.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productsStore.presentation.elm.productsList.ProductsListEvent
import com.example.productsStore.presentation.elm.productsList.ProductsListIntent
import com.example.productsStore.presentation.elm.productsList.ProductsListNews
import com.example.productsStore.presentation.elm.productsList.ProductsListState
import com.example.productsStore.presentation.elm.productsList.ProductsListStoreFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.plus
import ru.tinkoff.kotea.core.Store
import javax.inject.Inject

@HiltViewModel
class ProductsListViewModel @Inject constructor(
    private val productsListStoreFactory: ProductsListStoreFactory,
) : ViewModel() {
    private val store: Store<ProductsListState, ProductsListEvent, ProductsListNews> =
        productsListStoreFactory.create()

    val state: StateFlow<ProductsListState> = store.state

    val news: Flow<ProductsListNews> = store.news

    init {
        store.launchIn(
            coroutineScope = viewModelScope + Dispatchers.Unconfined
        )
    }

    fun acceptIntent(
        intent: ProductsListIntent,
    ) {
        store.dispatch(
            ProductsListEvent.UserIntent(intent)
        )
    }
}