package com.example.productsStore.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productsStore.domain.useCase.ClearCartUseCase
import com.example.productsStore.domain.useCase.ObserveCartProductsUseCase
import com.example.productsStore.presentation.state.CartUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val observeCartProductsUseCase: ObserveCartProductsUseCase,
    private val clearCartUseCase: ClearCartUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        observeCartProducts()
    }

    fun clearCart() {
        viewModelScope.launch {
            clearCartUseCase()
        }
    }

    private fun observeCartProducts() {
        viewModelScope.launch {
            observeCartProductsUseCase()
                .collectLatest { products ->
                    _uiState.value = if (products.isEmpty()) {
                        CartUiState.Empty
                    } else {
                        CartUiState.Success(products = products)
                    }
                }
        }
    }
}