package com.example.currencyconverter.domain

import java.util.Date

data class ExchangeRate(
    val result: String,
    val documentation: String,
    val termsOfUse: String,
    val date: Date,
    val baseCode: String,
    val conversionRates: Map<String, Double>
)

