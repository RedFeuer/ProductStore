package com.example.productsStore.domain.model

/** модель списка товаров с характеристиками для подгрузки */
data class ProductsPageModel(
    val products: List<ProductPreviewModel>,
    /** общее количество заметок на сервер */
    val total: Int,
    /** сколько элементов пропускаем от начала список
     * аналогично offset при работе с IO */
    val skip: Int,
    /** сколько элементов сервер вернет за один запрос */
    val limit: Int,
)
