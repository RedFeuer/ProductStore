package com.example.productsStore.presentation.navigation

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.productsStore.presentation.elm.cart.CartMessage
import com.example.productsStore.presentation.elm.cart.CartNews
import com.example.productsStore.presentation.ui.cart.CartScreen
import com.example.productsStore.presentation.viewModel.CartViewModel
import com.example.productsstore.R
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CartRoute(
    onBackClick: () -> Unit,
    onProductClick: (Int) -> Unit,
) {
    val viewModel: CartViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.news.collectLatest { news ->
            when (news) {
                CartNews.NavigateBack -> {
                    onBackClick()
                }

                is CartNews.OpenProductDetails -> {
                    onProductClick(news.productId)
                }

                is CartNews.ShowMessage -> {
                    Toast.makeText(
                        context,
                        news.message.asString(context),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    CartScreen(
        state = state,
        onIntent = { intent ->
            viewModel.acceptIntent(intent)
        }
    )
}

private fun CartMessage.asString(context: Context): String {
    return when (this) {
        CartMessage.CartCleared -> {
            context.getString(R.string.cart_cleared)
        }

        CartMessage.ProductNotFound -> {
            context.getString(R.string.product_not_found)
        }

        is CartMessage.Raw -> {
            message
        }
    }
}