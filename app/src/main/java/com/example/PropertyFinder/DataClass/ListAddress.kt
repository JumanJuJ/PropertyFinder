package com.example.PropertyFinder.DataClass

data class RootAddress(
    val data: ListAddress? = null
)

data class ListAddress(
    val _id: String? = null,
    val inputAddress: String? = null,
    val timestamp: String? = null,
    val success: Boolean? = null,
    val type: String? = null,
    val results: ResultAddress? = null
)

data class ResultAddress (
    val bathrooms: String? = null,
    val bedrooms: String? = null,
    val country: String? = null,
    val livingArea: Int? = null,
    val livingStatus: String? = null,
    val location: Location? = null,
    val lotSizeWithUnit: LotSizeUnit? = null,
    val media: Media? = null,
    val price: Price? = null,
    val propertyType: String? = null,
    val yearBuilt: Int? = null,
    val zpid : Int? = null,
)
