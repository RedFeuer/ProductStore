package com.example.productsStore.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productsStore.domain.useCase.ObserveProductPreviewsUseCase
import com.example.productsStore.domain.useCase.RefreshProductsPageUseCase
import com.example.productsStore.presentation.state.ProductListUiState
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
class ProductsListViewModel @Inject constructor(
    private val observeProductPreviewsUseCase: ObserveProductPreviewsUseCase,
    private val refreshProductsPageUseCase: RefreshProductsPageUseCase,
) : ViewModel() {
    /** состояние UI */
    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState : StateFlow<ProductListUiState> = _uiState.asStateFlow()

    /** размер списка товаров */
    private var pageSize: Int? = null
    /** сколько товаров сейчас нужно отображать из Room */
    private var loadedLimit: Int = 0
    /** с какой позиции делается следующий запрос. аналог offset */
    private var nextSkip: Int = 0
    /** всего товаров */
    private var totalProducts: Int? = null

    /** Job запроса товаров из БД */
    private var observeProductsJob: Job? = null
    /** Job обработки запроса */
    private var loadingJob : Job? = null

    fun loadInitialPage(calculatedPageSize: Int) {
        if (pageSize != null) return

        pageSize = calculatedPageSize
        loadedLimit = calculatedPageSize
        nextSkip = 0
        totalProducts = null

        observeProductsFromCache(limit = loadedLimit)

        refreshPage(
            limit = calculatedPageSize,
            skip = FIRST_PAGE_SKIP,
            isInitialLoading = true,
        )
    }

    fun retryInitialLoading() {
        val currentPageSize = pageSize ?: return

        loadedLimit = currentPageSize
        nextSkip = 0
        totalProducts = null

        observeProductsFromCache(limit = loadedLimit)

        refreshPage(
            limit = currentPageSize,
            skip = FIRST_PAGE_SKIP,
            isInitialLoading = true,
        )
    }

    fun loadNextPage() {
        val currentPageSize = pageSize ?: return
        val currentState = _uiState.value as? ProductListUiState.Success ?: return

        if (loadingJob?.isActive == true) return
        if (currentState.endReached) return
        if (currentState.pageErrorMessage != null) return

        val total = totalProducts
        if (total != null && nextSkip >= total) {
            _uiState.value = currentState.copy(endReached = true)
            return
        }

        val requestSkip = nextSkip

        loadedLimit += currentPageSize

        observeProductsFromCache(limit = loadedLimit)

        refreshPage(
            limit = currentPageSize,
            skip = requestSkip,
            isInitialLoading = false,
        )
    }

    fun retryNextPage() {
        val currentState = _uiState.value as? ProductListUiState.Success ?: return
        val currentPageSize = pageSize ?: return

        if (loadingJob?.isActive == true) return
        if (currentState.endReached) return

        _uiState.value = currentState.copy(
            pageErrorMessage = null,
        )

        refreshPage(
            limit = currentPageSize,
            skip = nextSkip,
            isInitialLoading = false,
        )
    }

    /** получаем товары из ДБ (возможно неактуальные) */
    private fun observeProductsFromCache(
        limit: Int,
    ) {
        observeProductsJob?.cancel()

        observeProductsJob = viewModelScope.launch {
            observeProductPreviewsUseCase(limit = limit)
                .collectLatest { products ->
                    if (products.isEmpty()) {
                        val currentState = _uiState.value

                        if (currentState !is ProductListUiState.Success) {
                            _uiState.value = ProductListUiState.Loading
                        }
                        return@collectLatest
                    }

                    val currentSuccess = _uiState.value as? ProductListUiState.Success

                    nextSkip = maxOf(nextSkip, products.size)

                    val endReachedByTotal = totalProducts?.let { total ->
                        products.size >= total
                    } ?: false

                    _uiState.value = ProductListUiState.Success(
                        products = products,
                        isPageLoading = currentSuccess?.isPageLoading ?: false,
                        pageErrorMessage = currentSuccess?.pageErrorMessage,
                        endReached = currentSuccess?.endReached == true || endReachedByTotal,
                    )
                }
        }
    }

    /** получение данных из API и сохранение в БД */
    private fun refreshPage(
        limit: Int,
        skip: Int,
        isInitialLoading: Boolean,
    ) {
        if (loadingJob?.isActive == true) return

        loadingJob = viewModelScope.launch {
            try {
                if (!isInitialLoading) {
                    val currentState = _uiState.value as? ProductListUiState.Success

                    if (currentState != null) {
                        _uiState.value = currentState.copy(
                            isPageLoading = true,
                            pageErrorMessage = null,
                        )
                    }
                }

                val productsPage = refreshProductsPageUseCase(
                    limit = limit,
                    skip = skip,
                )

                totalProducts = productsPage.total

                val loadedProducts = productsPage.products

                nextSkip = maxOf(nextSkip, (skip + loadedProducts.size))

                val isEndReached = loadedProducts.isEmpty() || nextSkip >= productsPage.total

                val currentState = _uiState.value as? ProductListUiState.Success

                if (currentState != null) {
                    _uiState.value = currentState.copy(
                        isPageLoading = false,
                        pageErrorMessage = null,
                        endReached = isEndReached,
                    )
                } else if (loadedProducts.isEmpty()) {
                    _uiState.value = ProductListUiState.Empty
                }
            } catch(exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                handleRefreshError(
                    exception = exception,
                    isInitialLoading = isInitialLoading,
                )
            }
        }
    }

    private fun handleRefreshError(
        exception: Exception,
        isInitialLoading: Boolean,
    ) {
        val currentState = _uiState.value

        if (currentState is ProductListUiState.Success && currentState.products.isNotEmpty()) {
            _uiState.value = currentState.copy(
                isPageLoading = false,
                pageErrorMessage = if (isInitialLoading) {
                    exception.message ?: DEFAULT_REFRESH_ERROR_MESSAGE
                } else {
                    exception.message ?: DEFAULT_PAGE_ERROR_MESSAGE
                }
            )
        } else {
            _uiState.value = ProductListUiState.Error(
                message = exception.message ?: DEFAULT_ERROR_MESSAGE
            )
        }
    }

    private companion object {
        const val FIRST_PAGE_SKIP = 0
        const val DEFAULT_ERROR_MESSAGE = "Не удалось загрузить товары"
        const val DEFAULT_REFRESH_ERROR_MESSAGE = "Не удалось обновить товары"
        const val DEFAULT_PAGE_ERROR_MESSAGE = "Не удалось загрузить следующую страницу"
    }
}