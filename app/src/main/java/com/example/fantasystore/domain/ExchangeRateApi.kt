package com.example.fantasystore.domain

import java.util.Date

data class ExchangeRateApi(
    val result: String,
    val documentation: String? = null,
    val termsOfUse: String? = null,
    val timeLastUpdateUnix: Date? = null,
    val timeLastUpdateUtc: Date? = null,
    val timeNextUpdateUtc: Date? = null, val baseCode: String,
    val targetCode: String,
    val conversionRate: Double,
    val conversionResult: Double
)