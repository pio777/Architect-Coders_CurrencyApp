package com.example.currencyconverter.data.mappers

import com.example.currencyconverter.data.ExchangeRateTimeResult
import com.example.currencyconverter.domain.ExchangeRateTime
import java.text.SimpleDateFormat
import java.util.Locale

object ExchangeRateTimeMapper {
    fun toDomain(response: ExchangeRateTimeResult): ExchangeRateTime {
        val parseDate = { dateString: String ->
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString)
                ?: throw IllegalArgumentException("Invalid date format")
        }

        return ExchangeRateTime(
            amount = response.amount,
            base = response.base,
            startDate = parseDate(response.startDate),
            endDate = parseDate(response.endDate),
            rates = response.rates.mapKeys { (dateString, _) -> parseDate(dateString) }
        )
    }
}
