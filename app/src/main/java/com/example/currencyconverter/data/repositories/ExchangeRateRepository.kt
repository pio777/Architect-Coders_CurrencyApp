package com.example.currencyconverter.data.repositories

import com.example.currencyconverter.data.clients.ExchangeRateClient
import com.example.currencyconverter.data.mappers.ExchangeHistoryMapper
import com.example.currencyconverter.data.mappers.ExchangeRateMapper
import com.example.currencyconverter.domain.ExchangeHistory
import com.example.currencyconverter.domain.ExchangeRate
import com.example.currencyconverter.domain.ExchangeRateApi

class ExchangeRateRepository(private val exchangeRateMapper: ExchangeRateMapper, private val exchangeHistoryMapper: ExchangeHistoryMapper) {

    suspend fun getPairConversion(base: String, target: String, amount: Float): ExchangeRateApi {
        val result = ExchangeRateClient
            .retrofitInstance
            .getPairConversion(base, target, amount)

        return exchangeRateMapper.toDomain(result)
    }

    suspend fun getCurrencyHistory(currency: String, year: Int, month: Int, day: Int): ExchangeHistory {
        val result = ExchangeRateClient
            .retrofitInstance
            .getCurrencyHistory(currency, year, month, day)

        return exchangeHistoryMapper.toDomain(result)
    }
}
