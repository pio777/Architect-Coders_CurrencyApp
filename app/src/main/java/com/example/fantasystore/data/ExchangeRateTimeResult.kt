package com.example.fantasystore.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ExchangeRateTimeResult(
    val amount: Float,
    val base: String,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String,
    val rates: Map<String, Map<String, Double>>
)