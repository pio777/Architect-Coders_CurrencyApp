package com.example.currencyconverter.domain.strategies

import com.example.currencyconverter.domain.Currency

interface ConversionStrategy {
    suspend fun convert(amount: Double, fromCurrency: Currency, toCurrency: Currency): Double
}