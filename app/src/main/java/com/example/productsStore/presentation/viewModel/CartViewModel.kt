package com.example.productsStore.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productsStore.presentation.elm.cart.CartEvent
import com.example.productsStore.presentation.elm.cart.CartIntent
import com.example.productsStore.presentation.elm.cart.CartNews
import com.example.productsStore.presentation.elm.cart.CartState
import com.example.productsStore.presentation.elm.cart.CartStoreFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import ru.tinkoff.kotea.core.Store
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    cartStoreFactory: CartStoreFactory,
) : ViewModel() {
    private val store: Store<CartState, CartEvent, CartNews> =
        cartStoreFactory.create()

    val state: StateFlow<CartState> = store.state

    val news: Flow<CartNews> = store.news

    init {
        store.launchIn(
            coroutineScope = viewModelScope + Dispatchers.Unconfined,
        )
    }

    fun acceptIntent(intent: CartIntent) {
        store.dispatch(
            event = CartEvent.UserIntent(intent)
        )
    }
}