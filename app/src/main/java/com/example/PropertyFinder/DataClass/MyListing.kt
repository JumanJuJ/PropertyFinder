package com.example.PropertyFinder.DataClass

data class AddListing(
    val address: String,
    val location: String,
    val livingArea: Int,
    val lotSize: Double,
    val propertyType: String,
    val yearBuilt: Int
)

data class AddMyListingRequest(
    val userId: String,
    val result: AddListing
)
