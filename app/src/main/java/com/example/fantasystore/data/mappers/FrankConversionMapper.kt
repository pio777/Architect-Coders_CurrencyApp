package com.example.fantasystore.data.mappers

import com.example.fantasystore.data.FrankConversionResult
import com.example.fantasystore.domain.FrankConversion
import java.text.SimpleDateFormat
import java.util.Locale

object FrankConversionMapper {
    fun toDomain(response: FrankConversionResult): FrankConversion {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(response.date)
            ?: throw IllegalArgumentException("Invalid date format")

        return FrankConversion(
            amount = response.amount,
            base = response.base,
            date = date,
            rates = response.rates
        )
    }
}