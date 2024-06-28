package com.example.currencyconverter.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRateTimeResult(
    val amount: Float,
    val base: String,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String,
    val rates: Map<String, Map<String, Double>>
)