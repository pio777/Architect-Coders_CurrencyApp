package com.example.fantasystore.domain

sealed class ErrorType {
    data object Network : ErrorType()
    data object Timeout : ErrorType()
    data object Unknown : ErrorType()
}