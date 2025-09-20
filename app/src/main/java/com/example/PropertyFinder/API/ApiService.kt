package com.example.PropertyFinder.API

import com.example.PropertyFinder.DataClass.AddMyListingRequest
import com.example.PropertyFinder.DataClass.FavouriteResponse
import com.example.PropertyFinder.DataClass.Favourites
import com.example.PropertyFinder.DataClass.MyListingsResponse
import com.example.PropertyFinder.DataClass.PriceRequest
import com.example.PropertyFinder.DataClass.PriceResponse
import com.example.PropertyFinder.DataClass.RootAddress
import com.example.PropertyFinder.DataClass.RootDetails
import com.example.PropertyFinder.DataClass.RootListDetails
import com.example.PropertyFinder.DataClass.RootResponse
import com.example.PropertyFinder.DataClass.User
import com.example.PropertyFinder.DataClass.ServerResponse
import com.example.PropertyFinder.DataClass.TrendingCard
import retrofit2.http.Query

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET

interface ApiService {

    @POST("/login")
    fun login(@Body request: User): Call<ServerResponse>

    @POST("/register")
    fun register(@Body request: User): Call<ServerResponse>

    @GET("/getListing")
    fun getListing(@Query("location") location: String): Call<RootResponse>

    @GET("getListing")
    fun getListingFilters(
        @Query("location") location: String,
        @Query("filters") filters: List<String>?,
        @Query("priceMin") priceMin: Int? = null,
        @Query("priceMax") priceMax: Int? = null,
        @Query("livingAreaMin") livingAreaMin: Int? = null,
        @Query("livingAreaMax") livingAreaMax: Int? = null,
        @Query("lotSizeMin") lotSizeMin: Int? = null,
        @Query("lotSizeMax") lotSizeMax: Int? = null,
    ): Call<RootResponse>

    @GET("/getListing")
    fun getListingTrending(
        @Query("location") location: String,
        @Query("trending") trendingCity: Boolean
    ): Call <TrendingCard>

    @GET("/getListingAddress")
    fun getListingAddress(@Query("address") address: String): Call<RootAddress>

    @GET("/getListingAddress")
    fun getListingAddressDetails(
        @Query("address") address: String,
        @Query("details") details: Boolean = true
    ): Call<RootDetails>

    @GET("/getUsername")
    fun getUsername(@Query("userId") userId: String): Call<ServerResponse>
    @POST("/addFavourites")
    fun addFavourites( @Body request: Favourites): Call <ServerResponse>

    @GET("/getFavourites")
    fun getFavourites(@Query("userId") userId: String): Call<FavouriteResponse>

    @GET("/getListingLocationFromArray")
    fun getListingLocationFromArray(@Query("ArrayId") ids: Array<String>?): Call<RootListDetails>

    @POST("addMyListings")
    fun addMyListings(@Body request: AddMyListingRequest): Call<ServerResponse>

    @GET("getMyListings")
    fun getMyListings(@Query("userId") userId: String): Call<MyListingsResponse>

    @POST("predictPrice")
    fun predictPrice(@Body request: PriceRequest): Call<PriceResponse>








}