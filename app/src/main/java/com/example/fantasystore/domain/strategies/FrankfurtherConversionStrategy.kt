package com.example.fantasystore.domain.strategies

import com.example.fantasystore.data.repositories.FrankfurterRateRepository
import com.example.fantasystore.domain.Currency

class FrankfurterConversionStrategy(private val repository: FrankfurterRateRepository) : ConversionStrategy {
    override suspend fun convert(amount: Double, fromCurrency: Currency, toCurrency: Currency): Double {
        val targetCurrencyCode = toCurrency.code
        return repository.getPairConversion(amount, fromCurrency.code, targetCurrencyCode).rates[targetCurrencyCode] ?: 0.0
    }
}