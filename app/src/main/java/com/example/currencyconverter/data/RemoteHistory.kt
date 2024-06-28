package com.example.currencyconverter.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRateResult(
    val result: String,
    val documentation: String? = null,
    @SerialName("terms_of_use") val termsOfUse: String? = null,
    @SerialName("time_last_update_unix") val timeLastUpdateUnix: Long? = null,
    @SerialName("time_last_update_utc") val timeLastUpdateUtc: String? = null,
    @SerialName("time_next_update_unix") val timeNextUpdateUnix: Long? = null,
    @SerialName("time_next_update_utc") val timeNextUpdateUtc: String? = null,
    @SerialName("base_code") val baseCode: String,
    @SerialName("target_code") val targetCode: String,
    @SerialName("conversion_rate") val conversionRate: Double,
    @SerialName("conversion_result") val conversionResult: Double
)