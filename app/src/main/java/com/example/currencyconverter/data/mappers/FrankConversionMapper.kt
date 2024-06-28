package com.example.currencyconverter.data.mappers

import com.example.currencyconverter.data.FrankConversionResult
import com.example.currencyconverter.domain.FrankConversion
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