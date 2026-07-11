package com.example.productsStore.presentation.navigation

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.productsStore.presentation.elm.productDetails.ProductDetailsMessage
import com.example.productsStore.presentation.elm.productDetails.ProductDetailsNews
import com.example.productsStore.presentation.elm.productDetails.toUiState
import com.example.productsStore.presentation.ui.productDetails.ProductDetailsScreen
import com.example.productsStore.presentation.viewModel.ProductDetailsViewModel
import com.example.productsstore.R
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProductDetailsRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: ProductDetailsViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.news.collectLatest { news ->
            when (news) {
                ProductDetailsNews.NavigateBack -> {
                    onBackClick()
                }

                is ProductDetailsNews.ShowMessage -> {
                    val message = news.message
                    if (message is ProductDetailsMessage.Raw) {
                        Toast.makeText(
                            context,
                            news.message.asString(context),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    ProductDetailsScreen(
        state = state.toUiState(),
        onIntent = { intent ->
            viewModel.acceptIntent(intent)
        },
    )
}

private fun ProductDetailsMessage.asString(context: Context): String {
    return when (this) {
        ProductDetailsMessage.ProductNotLoadedYet -> {
            context.getString(R.string.product_not_loaded_yet)
        }

        ProductDetailsMessage.ProductAddedToCart -> {
            context.getString(R.string.product_added_to_cart)
        }

        is ProductDetailsMessage.Raw -> {
            message
        }
    }
}