package com.example.productsStore.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productsStore.domain.useCase.GetProductsPageUseCase
import com.example.productsStore.presentation.state.ProductListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsListViewModel @Inject constructor(
    private val getProductsPageUseCase: GetProductsPageUseCase,
) : ViewModel() {
    /** состояние UI */
    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState : StateFlow<ProductListUiState> = _uiState.asStateFlow()

    /** размер списка товаров */
    private var pageSize: Int? = null
    /** с какой позиции делается следующий запрос. аналог offset */
    private var nextSkip: Int = 0
    /** всего товаров */
    private var totalProducts: Int? = null

    /** Job обработки запроса */
    private var loadingJob : Job? = null

    fun loadInitialPage(calculatedPageSize: Int) {
        if (pageSize != null) return

        pageSize = calculatedPageSize
        nextSkip = 0
        totalProducts = null

        loadPage(isInitialLoading = true)
    }

    fun retryInitialLoading() {
        if (pageSize == null) return

        nextSkip = 0
        totalProducts = null

        loadPage(isInitialLoading = true)
    }

    fun loadNextPage() {
        val currentState = _uiState.value as? ProductListUiState.Success ?: return

        if (loadingJob?.isActive == true) return
        if (currentState.endReached) return
        if (currentState.pageErrorMessage != null) return

        val total = totalProducts
        if (total != null && nextSkip >= total) {
            _uiState.value = currentState.copy(endReached = true)
            return
        }

        loadPage(isInitialLoading = false)
    }

    /** загрузка страницы и обработка состояний UI в зависимости от того:
     * первая это загрузка или нет */
    private fun loadPage(isInitialLoading: Boolean) {
        val currentPageSize = pageSize ?: return

        if (loadingJob?.isActive == true) return

        loadingJob = viewModelScope.launch {
            if (isInitialLoading) {
                _uiState.emit(ProductListUiState.Loading)
            } else {
                val currentState = _uiState.value as? ProductListUiState.Success
                if (currentState != null) {
                    _uiState.value = currentState.copy(
                        isPageLoading = true,
                        pageErrorMessage = null,
                    )
                }
            }

            try {
                val productsPage = getProductsPageUseCase(
                    limit = currentPageSize,
                    skip = nextSkip,
                )

                totalProducts = productsPage.total

                val loadedProducts = productsPage.products
                nextSkip += loadedProducts.size

                val isEndReached = loadedProducts.isEmpty() || nextSkip >= productsPage.total

                if (isInitialLoading) {
                    _uiState.value = if (loadedProducts.isEmpty()) {
                        ProductListUiState.Empty
                    } else {
                        ProductListUiState.Success(
                            products = loadedProducts,
                            isPageLoading = false,
                            pageErrorMessage = null,
                            endReached = isEndReached,
                        )
                    }
                } else {
                    val previousProducts =
                        (_uiState.value as? ProductListUiState.Success)
                            ?.products
                            .orEmpty()

                    _uiState.emit(ProductListUiState.Success(
                        products = previousProducts + loadedProducts,
                        isPageLoading = false,
                        pageErrorMessage = null,
                        endReached = isEndReached,
                    ))
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                if (isInitialLoading) {
                    _uiState.emit(ProductListUiState.Error(
                        message = exception.message ?: DEFAULT_ERROR_MESSAGE
                    ))
                } else {
                    val currentState = _uiState.value as? ProductListUiState.Success
                    if (currentState != null) {
                        _uiState.value = currentState.copy(
                            isPageLoading = false,
                            pageErrorMessage = exception.message ?: DEFAULT_PAGE_ERROR_MESSAGE,
                        )
                    }
                }
            }
        }
    }

    private companion object {
        const val DEFAULT_ERROR_MESSAGE = "Не удалось загрузить товары"
        const val DEFAULT_PAGE_ERROR_MESSAGE = "Не удалось загрузить следующую страницу"
    }
}