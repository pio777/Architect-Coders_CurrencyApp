package com.example.fantasystore.domain.strategies

import com.example.fantasystore.domain.Currency

interface ConversionStrategy {
    suspend fun convert(amount: Double, fromCurrency: Currency, toCurrency: Currency): Double
}