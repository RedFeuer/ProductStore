package com.example.productsStore.presentation.elm.productDetails

import com.example.productsStore.domain.model.ProductDetailsModel
import org.junit.Test

        // GIVEN

        // WHEN

        // THEN

class ProductDetailsUpdateTest {



    private fun createProductDetailsModel(
        id: Int = 1,
        title: String = "Product",
        price: Double = 10.0,
        brand: String? = "Brand",
        description: String = "Descriprion",
        rating: Double = 4.5,
        weight: Int = 2,
        availabilityStatus: String = "In stock",
        warrantyInformation: String = "Warranty",
        imageUrl: String? = "https://example.com/image.png",
    ) : ProductDetailsModel {
        return ProductDetailsModel(
            id = id,
            title = title,
            price = price,
            brand = brand,
            description = description,
            rating = rating,
            weight = weight,
            availabilityStatus = availabilityStatus,
            warrantyInformation = warrantyInformation,
            imageUrl = imageUrl,
        )
    }

    private companion object {
        const val PRODUCT_ID = 1
        const val NETWORK_ERROR_MESSAGE = "Network error"
    }
}