package com.example.fantasystore.data

import kotlinx.serialization.Serializable

@Serializable
data class FrankConversionResult(
    val amount: Double,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)
