package com.example.productsStore.presentation.navigation

sealed class Screen(
    val route : String
) {
    data object ProductsList : Screen("products_list")

    data object Cart : Screen("cart")

    data object ProductDetails : Screen("product_details/{productId}") {
        private const val BASE_ROUTE = "product_details"

        fun createRoute(productId : Int) : String {
            return "$BASE_ROUTE/$productId"
        }
    }
}