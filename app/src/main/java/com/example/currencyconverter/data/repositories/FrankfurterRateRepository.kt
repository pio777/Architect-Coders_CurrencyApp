package com.example.currencyconverter.data.repositories

import com.example.currencyconverter.data.clients.FrankfurterRateClient
import com.example.currencyconverter.data.mappers.ExchangeRateTimeMapper
import com.example.currencyconverter.data.mappers.FrankConversionMapper
import com.example.currencyconverter.domain.ExchangeRateTime
import com.example.currencyconverter.domain.FrankConversion

class FrankfurterRateRepository(
    private val frankConversionMapper: FrankConversionMapper,
    private val exchangeRateTimeMapper: ExchangeRateTimeMapper
) {

    suspend fun getPairConversion(amount: Double, from: String, to: String): FrankConversion {
        val conversionResult = FrankfurterRateClient
            .retrofitInstance
            .getPairConversion(amount, from, to)
        return frankConversionMapper.toDomain(conversionResult)
    }

    suspend fun getHistoricalConversion(
        startDate: String,
        endDate: String,
        from: String,
        to: String
    ): ExchangeRateTime {
        val historicalConversionResult = FrankfurterRateClient
            .retrofitInstance
            .getHistoricalConversion(startDate, endDate, from, to)
        return exchangeRateTimeMapper.toDomain(historicalConversionResult)
    }
}
