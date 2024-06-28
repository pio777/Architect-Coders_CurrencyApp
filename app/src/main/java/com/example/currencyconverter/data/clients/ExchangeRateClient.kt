package com.example.currencyconverter.data.clients

import com.example.currencyconverter.BuildConfig
import com.example.currencyconverter.data.services.ExchangeRateService
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create

private const val EXCHANGERATE_BASE_URL = "https://v6.exchangerate-api.com/v6/"
private const val APPLICATION_JSON = "application/json"

object ExchangeRateClient {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(::apiKeyAsPathInterceptor)
        .build()
    private val json = Json { ignoreUnknownKeys = true }

    val retrofitInstance = Retrofit.Builder()
        .baseUrl(EXCHANGERATE_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory(APPLICATION_JSON.toMediaType()))
        .build()
        .create<ExchangeRateService>()

    private fun apiKeyAsPathInterceptor(chain: Interceptor.Chain) = chain.proceed(
        chain
            .request()
            .newBuilder()
            .url(
                chain.request().url.toString()
                    .replace("v6/", "v6/${BuildConfig.EXCHANGE_RATE_API_KEY}/")
                    .toHttpUrl()
            )
            .build()
    )
}