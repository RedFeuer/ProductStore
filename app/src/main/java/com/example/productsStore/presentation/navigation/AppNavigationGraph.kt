package com.example.productsStore.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

/** граф навигации Jetpack Navigation */
@Composable
fun AppNavigationGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.ProductsList.route,
    ) {
        /* список приложений */
        composable(
            route = Screen.ProductsList.route
        ) {
            ProductsListRoute(
                onProductClick = { productId ->
                    navController.navigate(
                        route = Screen.ProductDetails.createRoute(productId)
                    )
                },
                onCartClick = { navController.navigate(Screen.Cart.route) }
            )
        }

        /* конкретное приложение */
        composable(
            route = Screen.ProductDetails.route,
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.IntType
                }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "products-store://product-details/{productId}"
                }
            ),
        ) {
            ProductDetailsRoute(
                onBackClick = { navController.popBackStack() }
            )
        }

        /* корзина с товарами */
        composable(
            route = Screen.Cart.route,
        ) {
            CartRoute(
                onBackClick = { navController.popBackStack() },
                onProductClick = { productId ->
                    navController.navigate(
                        route = Screen.ProductDetails.createRoute(productId)
                    )
                }
            )
        }
    }
}