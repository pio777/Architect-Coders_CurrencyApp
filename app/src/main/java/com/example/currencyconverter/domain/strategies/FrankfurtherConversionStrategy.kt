package com.example.currencyconverter.domain.strategies

import com.example.currencyconverter.data.repositories.FrankfurterRateRepository
import com.example.currencyconverter.domain.Currency

class FrankfurterConversionStrategy(private val repository: FrankfurterRateRepository) : ConversionStrategy {
    override suspend fun convert(amount: Double, fromCurrency: Currency, toCurrency: Currency): Double {
        val targetCurrencyCode = toCurrency.code
        return repository.getPairConversion(amount, fromCurrency.code, targetCurrencyCode).rates[targetCurrencyCode] ?: 0.0
    }
}