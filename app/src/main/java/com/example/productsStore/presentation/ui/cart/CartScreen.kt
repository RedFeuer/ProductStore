package com.example.productsStore.presentation.ui.cart

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.productsStore.presentation.elm.cart.CartIntent
import com.example.productsStore.presentation.elm.cart.CartState
import com.example.productsStore.presentation.ui.CartTestTags
import com.example.productsstore.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    state: CartState,
    onIntent: (CartIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.cart))
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onIntent(CartIntent.BackClicked) },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                actions = {
                    if (state is CartState.Success && state.products.isNotEmpty()) {
                        Button(
                            onClick = { onIntent(CartIntent.ClearCartClicked) },
                            modifier = Modifier.testTag(CartTestTags.CLEAR_CART_BUTTON),
                        ) {
                            Text(text = stringResource(R.string.clear))
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        when (state) {
            is CartState.Success -> {
                CartContent(
                    products = state.products,
                    onIntent = onIntent,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            CartState.Loading -> {
                CartLoading(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp)
                )
            }

            CartState.Empty -> {
                CartEmpty(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            is CartState.Error -> {
                CartError(
                    message = state.message,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }
}