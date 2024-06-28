package com.example.fantasystore.domain

import java.util.Date

data class FrankConversion(
    val amount: Double,
    val base: String,
    val date: Date,
    val rates: Map<String, Double>
)