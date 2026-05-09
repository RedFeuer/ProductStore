package com.example.productsStore.data.remote.api

import com.example.productsStore.data.remote.dto.ProductDetailsDto
import com.example.productsStore.data.remote.dto.ProductsPageDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductsApi {
    @GET("products")
    suspend fun getProductsPage(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int,
        @Query("select") select: String,
    ) : ProductsPageDto

    @GET
    suspend fun getProductDetails(@Path("id") id: Int) : ProductDetailsDto
}