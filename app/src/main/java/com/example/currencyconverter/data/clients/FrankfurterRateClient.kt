package com.example.currencyconverter.data.clients

import com.example.currencyconverter.data.services.FrankfurtherRateService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create

private const val APPLICATION_JSON = "application/json"
private const val FRANKFURTER_BASE_URL = "https://api.frankfurter.app/"

object FrankfurterRateClient {

    private val okHttpClient = OkHttpClient.Builder()
        //.addInterceptor(::apiKeyAsQueryInterceptor)
        .build()
    private val json = Json { ignoreUnknownKeys = true }

    val retrofitInstance = Retrofit.Builder()
        .baseUrl(FRANKFURTER_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory(APPLICATION_JSON.toMediaType()))
        .build()
        .create<FrankfurtherRateService>()
}