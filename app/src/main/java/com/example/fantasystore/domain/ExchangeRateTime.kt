package com.example.fantasystore.domain

import java.util.Date

data class ExchangeRateTime(
    val amount: Float,
    val base: String,
    val startDate: Date,
    val endDate: Date,
    val rates: Map<Date, Map<String, Double>>
)