package com.example.currencyconverter.data.mappers

import com.example.currencyconverter.data.ExchangeRateResult
import com.example.currencyconverter.domain.ExchangeRate
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExchangeRateMapper {
    private val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)

    fun toDomain(response: ExchangeRateResult): ExchangeRate {
        val lastUpdateDate = response.timeLastUpdateUtc?.let {
            try {
                dateFormat.parse(it)
            } catch (e: ParseException) {
                null
            }
        }
        val nextUpdateDate = response.timeNextUpdateUtc?.let {
            try {
                dateFormat.parse(it)
            } catch (e: ParseException) {
                null
            }
        }

        return ExchangeRate(
            result = response.result,
            documentation = response.documentation,
            termsOfUse = response.termsOfUse,
            timeLastUpdateUnix = response.timeLastUpdateUnix?.let { Date(it * 1000) },
            timeLastUpdateUtc = lastUpdateDate,
            timeNextUpdateUtc = nextUpdateDate,
            baseCode = response.baseCode,
            targetCode = response.targetCode,
            conversionRate = response.conversionRate,
            conversionResult = response.conversionResult
        )
    }
}