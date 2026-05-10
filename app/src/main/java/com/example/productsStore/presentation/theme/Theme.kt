package com.example.productsStore.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.productsStore.presentation.ui.theme.DarkBackground
import com.example.productsStore.presentation.ui.theme.DarkError
import com.example.productsStore.presentation.ui.theme.DarkOnBackground
import com.example.productsStore.presentation.ui.theme.DarkOnPrimary
import com.example.productsStore.presentation.ui.theme.DarkOnPrimaryContainer
import com.example.productsStore.presentation.ui.theme.DarkOnSurface
import com.example.productsStore.presentation.ui.theme.DarkPrimary
import com.example.productsStore.presentation.ui.theme.DarkPrimaryContainer
import com.example.productsStore.presentation.ui.theme.DarkSurface
import com.example.productsStore.presentation.ui.theme.DarkSurfaceContainer
import com.example.productsStore.presentation.ui.theme.DarkSurfaceContainerHigh
import com.example.productsStore.presentation.ui.theme.DarkSurfaceContainerLow
import com.example.productsStore.presentation.ui.theme.LightBackground
import com.example.productsStore.presentation.ui.theme.LightError
import com.example.productsStore.presentation.ui.theme.LightOnBackground
import com.example.productsStore.presentation.ui.theme.LightOnPrimary
import com.example.productsStore.presentation.ui.theme.LightOnPrimaryContainer
import com.example.productsStore.presentation.ui.theme.LightOnSurface
import com.example.productsStore.presentation.ui.theme.LightPrimary
import com.example.productsStore.presentation.ui.theme.LightPrimaryContainer
import com.example.productsStore.presentation.ui.theme.LightSurface
import com.example.productsStore.presentation.ui.theme.LightSurfaceContainer
import com.example.productsStore.presentation.ui.theme.LightSurfaceContainerHigh
import com.example.productsStore.presentation.ui.theme.LightSurfaceContainerLow

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,

    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    error = LightError,

    surfaceContainerLow = LightSurfaceContainerLow,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerHigh = LightSurfaceContainerHigh,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,

    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    error = DarkError,

    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
)

@Composable
fun ProductsStoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current

            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        darkTheme -> DarkColorScheme

        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}