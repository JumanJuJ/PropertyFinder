package com.example.PropertyFinder.API

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiClient {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:5000/") // per Flask locale
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()


    val client = retrofit.create(ApiService::class.java)

}