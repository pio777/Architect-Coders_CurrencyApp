package com.example.currencyconverter.domain

sealed class ErrorType {
    data object Network : ErrorType()
    data object Timeout : ErrorType()
    data object Unknown : ErrorType()
}