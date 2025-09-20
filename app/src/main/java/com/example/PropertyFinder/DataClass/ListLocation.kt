package com.example.PropertyFinder.DataClass

data class RootResponse(
    val data: ListLocation? = null
)

data class ListLocation(
    val _id: String? = null,
    val userId: String?= null,
    val inputLocation: String? = null,
    val timestamp: String? = null,
    val success: Boolean? = null,
    val type: String? = null,
    val results: List<Result>? = null
)

data class Result(
    val address: address? = null,
    val bathrooms: Int? = null,
    val bedrooms: Int? = null,
    val country: String? = null,
    val listingStatus: String? = null,
    val livingArea: Int? = null,
    val location: Location? = null,
    val lotSizeUnit: LotSizeUnit? = null,
    val media: Media? = null,
    val price: Price? = null,
    val propertyType: String? = null,

    val yearBuilt: Int? = null,
    val zpid: String? =null
)

data class address(
    val city: String? = null,
    val state: String? = null,
    val streetAddress: String? = null,
    val zipcode: Int? = null
)

data class Location(
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class LotSizeUnit(
    val lotSize: Float? = null,
    val lotSizeUnit: String? = null
)

data class Media(
    val allPropertyPhotos: AllPropertyPhotos? = null
)

data class AllPropertyPhotos(
    val highResolution: List<String>? = null
)

data class Price(
    val pricePerSquareFoot: Int? = null,
    val value: Int? = null
)
