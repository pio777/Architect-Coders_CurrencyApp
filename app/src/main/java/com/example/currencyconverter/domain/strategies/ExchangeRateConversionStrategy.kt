package com.example.currencyconverter.domain.strategies

import com.example.currencyconverter.data.repositories.ExchangeRateRepository
import com.example.currencyconverter.domain.Currency

class ExchangeRateConversionStrategy(private val repository: ExchangeRateRepository) : ConversionStrategy {
    override suspend fun convert(amount: Double, fromCurrency: Currency, toCurrency: Currency): Double {
        val exchangeRate = repository.getPairConversion(fromCurrency.code, toCurrency.code, amount.toFloat())
        return exchangeRate.conversionResult ?: 0.0
    }
}