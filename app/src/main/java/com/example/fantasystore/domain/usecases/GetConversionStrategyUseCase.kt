package com.example.fantasystore.domain.usecases

import com.example.fantasystore.data.repositories.ExchangeRateRepository
import com.example.fantasystore.data.repositories.FrankfurterRateRepository
import com.example.fantasystore.domain.Currency
import com.example.fantasystore.domain.frankfurterCurrenciesList
import com.example.fantasystore.domain.strategies.ConversionStrategy
import com.example.fantasystore.domain.strategies.ExchangeRateConversionStrategy
import com.example.fantasystore.domain.strategies.FrankfurterConversionStrategy


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