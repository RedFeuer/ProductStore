package com.example.productsStore.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductsPageDto(
    val products: List<ProductPreviewDto>,
    /** общее количество заметок на сервер */
    val total: Int,
    /** сколько элементов пропускаем от начала список
     * аналогично offset при работе с IO */
    val skip: Int,
    /** сколько элементов сервер вернет за один запрос */
    val limit: Int,
) {
}