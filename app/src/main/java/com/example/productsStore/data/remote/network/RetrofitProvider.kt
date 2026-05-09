package com.example.productsStore.data.remote.network

import com.example.productsStore.data.remote.api.ProductsApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitProvider {
    private const val BASE_URL = "https://dummyjson.com/"

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private val retrofit : Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(
            json.asConverterFactory("application/json".toMediaType())
        )
        .build()

    val productsApi : ProductsApi = retrofit.create(ProductsApi::class.java)
}