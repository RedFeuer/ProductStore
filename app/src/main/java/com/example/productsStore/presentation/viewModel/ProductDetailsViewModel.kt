package com.example.productsStore.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productsStore.domain.useCase.GetProductDetailsUseCase
import com.example.productsStore.presentation.state.ProductDetailsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDetailsViewModel(
    private val productId : Int,
    private val getProductDetailsUseCase: GetProductDetailsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProductDetailsUiState>(ProductDetailsUiState.Loading)
    val uiState : StateFlow<ProductDetailsUiState> = _uiState.asStateFlow()

    init {
        loadProductDetails()
    }

    fun loadProductDetails() {
        viewModelScope.launch {
            _uiState.emit(ProductDetailsUiState.Loading)

            runCatching {
                getProductDetailsUseCase(productId)
            }.onSuccess { productDetailsModel ->
                _uiState.emit(ProductDetailsUiState.Success(
                    product = productDetailsModel
                ))
            }.onFailure { throwable ->
                _uiState.emit(ProductDetailsUiState.Error(
                    message = throwable.message ?: ERROR_MESSAGE
                ))
            }
        }
    }

    private companion object {
        const val ERROR_MESSAGE = "Не удалось загрузить товар"
    }
}