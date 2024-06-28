package com.example.fantasystore.data.repositories

import com.example.fantasystore.data.clients.ExchangeRateClient
import com.example.fantasystore.data.mappers.ExchangeHistoryMapper
import com.example.fantasystore.data.mappers.ExchangeRateMapper
import com.example.fantasystore.domain.ExchangeHistory
import com.example.fantasystore.domain.ExchangeRate
import com.example.fantasystore.domain.ExchangeRateApi

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
