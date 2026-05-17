package com.example.productsStore.presentation.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productsStore.domain.useCase.ObserveProductDetailsUseCase
import com.example.productsStore.domain.useCase.RefreshProductDetailsIfNeededUseCase
import com.example.productsStore.presentation.state.ProductDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    savedStateHandle : SavedStateHandle,
    private val observeProductDetailsUseCase: ObserveProductDetailsUseCase,
    private val refreshProductDetailsIfNeededUseCase: RefreshProductDetailsIfNeededUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProductDetailsUiState>(ProductDetailsUiState.Loading)
    val uiState : StateFlow<ProductDetailsUiState> = _uiState.asStateFlow()

    private val productId : Int = checkNotNull(
        savedStateHandle.get<Int>(PRODUCT_ID_ARGUMENT)
    ) {
        "ID продукта не найден"
    }

    private var observeProductDetailsJob: Job? = null
    private var refreshProductDetailsJob: Job? = null

    init {
        observeCachedProductDetails()

    }

    private fun observeCachedProductDetails() {
        observeProductDetailsJob?.cancel()

        observeProductDetailsJob = viewModelScope.launch {
            observeProductDetailsUseCase(productId = productId)
                .collectLatest { cachedProductDetailsModel ->
                    if (cachedProductDetailsModel != null) {
                        _uiState.value = ProductDetailsUiState.Success(
                            product = cachedProductDetailsModel.product,
                            isStale = cachedProductDetailsModel.isStale,
                        )
                    } else {
                        val currentState = _uiState.value

                        if (currentState !is ProductDetailsUiState.Success) {
                            _uiState.emit(ProductDetailsUiState.Loading)
                        }
                    }
                }
        }
    }

    private fun refreshProductDetails() {
        if (refreshProductDetailsJob?.isActive == true) return

        refreshProductDetailsJob = viewModelScope.launch {
            try {
                refreshProductDetailsIfNeededUseCase(productId = productId)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                val currentState = _uiState.value

                if (currentState !is ProductDetailsUiState.Success) {
                    _uiState.value = ProductDetailsUiState.Error(
                        message = exception.message ?: ERROR_MESSAGE,
                    )
                }
            }
        }
    }

    private companion object {
        const val PRODUCT_ID_ARGUMENT = "productId"
        const val ERROR_MESSAGE = "Не удалось загрузить товар"
    }
}