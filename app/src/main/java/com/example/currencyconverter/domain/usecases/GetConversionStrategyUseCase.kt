package com.example.currencyconverter.domain.usecases

import com.example.currencyconverter.data.repositories.ExchangeRateRepository
import com.example.currencyconverter.data.repositories.FrankfurterRateRepository
import com.example.currencyconverter.domain.Currency
import com.example.currencyconverter.domain.frankfurterCurrenciesList
import com.example.currencyconverter.domain.strategies.ConversionStrategy
import com.example.currencyconverter.domain.strategies.ExchangeRateConversionStrategy
import com.example.currencyconverter.domain.strategies.FrankfurterConversionStrategy


class GetConversionStrategyUseCase(
    private val frankfurterRepository: FrankfurterRateRepository,
    private val exchangeRateRepository: ExchangeRateRepository
) {
    operator fun invoke(fromCurrency: Currency, toCurrency: Currency): ConversionStrategy {
        return if (frankfurterCurrenciesList.contains(fromCurrency) && frankfurterCurrenciesList.contains(toCurrency)) {
            FrankfurterConversionStrategy(frankfurterRepository)
        } else {
            ExchangeRateConversionStrategy(exchangeRateRepository)
        }
    }
}