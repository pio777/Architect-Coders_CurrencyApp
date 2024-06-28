package com.example.fantasystore.data.mappers

import com.example.fantasystore.data.ExchangeRateResult
import com.example.fantasystore.domain.ExchangeRateApi
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExchangeRateMapper {
    private val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)

    fun toDomain(response: ExchangeRateResult): ExchangeRateApi {
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

        return ExchangeRateApi(
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