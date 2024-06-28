package com.example.currencyconverter.domain

import java.util.Date

data class FrankConversion(
    val amount: Double,
    val base: String,
    val date: Date,
    val rates: Map<String, Double>
)