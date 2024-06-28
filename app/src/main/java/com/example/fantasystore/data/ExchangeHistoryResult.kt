package com.example.fantasystore.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ExchangeHistoryResult(
    val result: String,
    val documentation: String,
    @SerialName("terms_of_use") val termsOfUse: String,
    val year: Int,
    val month: Int,
    val day: Int,
    @SerialName("base_code") val baseCode: String,
    @SerialName("conversion_rates") val conversionRates: Map<String, Double>
)