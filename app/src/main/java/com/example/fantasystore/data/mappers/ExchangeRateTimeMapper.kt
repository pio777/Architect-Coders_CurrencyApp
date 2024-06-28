package com.example.fantasystore.data.mappers

import com.example.fantasystore.data.ExchangeRateTimeResult
import com.example.fantasystore.domain.ExchangeRateTime
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
