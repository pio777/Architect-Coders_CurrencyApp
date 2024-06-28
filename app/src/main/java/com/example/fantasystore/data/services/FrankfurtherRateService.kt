package com.example.fantasystore.data.services

import com.example.fantasystore.data.ExchangeRateTimeResult
import com.example.fantasystore.data.FrankConversionResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FrankfurtherRateService {

    @GET("{start_date}..{end_date}")
    suspend fun getHistoricalConversion(
        @Path("start_date") startDate: String,
        @Path("end_date") endDate: String,
        @Query("from") from: String,
        @Query("to") to: String
    ): ExchangeRateTimeResult

    @GET("latest")
    suspend fun getPairConversion(
        @Query("amount") amount: Double,
        @Query("from") from: String,
        @Query("to") to: String
    ): FrankConversionResult
}
