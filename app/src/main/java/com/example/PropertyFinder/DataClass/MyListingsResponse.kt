package com.example.PropertyFinder.DataClass


data class MyListingsResponse(
    val success: Boolean,
    val message: String?,
    val data: List<MyListingItem>?
)

data class MyListingItem(
    val _id: String,
    val userId: String,
    val myListing: AddListing
)
