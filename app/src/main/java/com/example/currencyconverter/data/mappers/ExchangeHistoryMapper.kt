package com.example.currencyconverter.data.mappers

import com.example.currencyconverter.data.ExchangeHistoryResult
import com.example.currencyconverter.domain.ExchangeHistory
import java.util.Calendar

object ExchangeHistoryMapper {

    fun toDomain(response: ExchangeHistoryResult): ExchangeHistory {
        val date = Calendar.getInstance().apply {
            set(response.year, response.month - 1, response.day)
        }.time // Get Date object

        return ExchangeHistory(
            result = response.result,
            documentation = response.documentation,
            termsOfUse = response.termsOfUse,
            date = date,
            baseCode = response.baseCode,
            conversionRates = response.conversionRates
        )
    }
}