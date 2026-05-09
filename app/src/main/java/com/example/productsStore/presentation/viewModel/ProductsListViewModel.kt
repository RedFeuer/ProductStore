package com.example.productsStore.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productsStore.domain.useCase.GetProductsPageUseCase
import com.example.productsStore.presentation.state.ProductListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductsListViewModel(
    private val getProductsPageUseCase: GetProductsPageUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState : StateFlow<ProductListUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.emit(ProductListUiState.Loading)

            runCatching {
                getProductsPageUseCase(
                    limit = PRODUCTS_LIMIT,
                    skip = FIRST_PAGE_SKIP
                )
            }.onSuccess { productsPageModel ->
                _uiState.value = if (productsPageModel.products.isEmpty()) {
                    ProductListUiState.Empty
                } else {
                    ProductListUiState.Success(products = productsPageModel.products)
                }
            }.onFailure { throwable ->
                _uiState.emit(ProductListUiState.Error(
                    message = throwable.message ?: ERROR_MESSAGE
                ))
            }
        }
    }

    private companion object {
        const val PRODUCTS_LIMIT = 20
        const val FIRST_PAGE_SKIP = 0
        const val ERROR_MESSAGE = "Не удалось загрузить товары"
    }
}