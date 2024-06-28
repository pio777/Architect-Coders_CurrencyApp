package com.example.currencyconverter.data.services

import com.example.currencyconverter.data.ExchangeHistoryResult
import com.example.currencyconverter.data.ExchangeRateResult
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateService {

    @GET("pair/{base}/{target}/{amount}")
    suspend fun getPairConversion(
        @Path("base") base: String,
        @Path("target") target: String,
        @Path("amount") amount: Float
    ): ExchangeRateResult

    @GET("history/{currency}/{year}/{month}/{day}")
    suspend fun getCurrencyHistory(
        @Path("currency") currency: String,
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Path("day") day: Int
    ): ExchangeHistoryResult
}
