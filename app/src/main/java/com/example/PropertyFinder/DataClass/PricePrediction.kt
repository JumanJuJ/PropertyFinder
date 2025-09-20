package com.example.PropertyFinder.DataClass

data class PriceRequest(
    val livingArea: Double,
    val lotSize: Double,
    val lotSizeUnit: String = "sqft",
    val yearBuilt: Int,
    val propertyType: String,
    val city: String
)

data class PriceResponse(
    val success: Boolean,
    val price: Double?,
    val message: String?
)
