package com.example.fantasystore.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRateError(
    @SerialName("result") val result: String,
    @SerialName("error-type") val errorType: String
)