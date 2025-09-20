package com.example.PropertyFinder.DataClass


data class TrendingCard(
    val TrendingList: List<TrendingCity>
)
data class TrendingCity(val address: String, val imageUrl: String)
