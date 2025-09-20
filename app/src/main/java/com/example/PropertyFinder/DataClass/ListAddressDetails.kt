package com.example.PropertyFinder.DataClass

data class RootDetails(
    val data: ListDetails? = null
)

data class RootListDetails (
    val data: List<ListDetails>? = null,
    val success: Boolean? = null
)

data class ListDetails(
    val _id: String? = null,
    val inputAddress: String? = null,
    val timestamp: String? = null,
    val success: Boolean? = null,
    val type: String? = null,
    val results: ResultDetails? = null
)

data class ResultDetails(
    val address: address? = null,
    val bathrooms: String? = null,
    val bedrooms: String? = null,
    val country: String? = null,
    val description: String? = null,
    val details: Details? = null,
    val hoaFee: Int? = null,
    val hoaFeeFrequency: String? = null,
    val livingArea: Int? = null,
    val listingStatus: String? = null,
    val location: Location? = null,
    val lotSizeUnit: LotSizeUnit? = null,
    val media: Media? = null,
    val price: Price? = null,
    val propertyType: String? = null,
    val yearBuilt: Int? = null,
    val zpid: String? = null
)

data class Details(
    val appliances: Array<String>? = null,
    val coolingInfo: Array<String>? = null,
    val flooring: Array<String>? = null,
    val garageInfo: Array<String>? = null,
    val heatingInfo: Array<String>? = null,
)
