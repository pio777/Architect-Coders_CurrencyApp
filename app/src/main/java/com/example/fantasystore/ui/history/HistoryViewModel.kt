package com.example.fantasystore.ui.history

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fantasystore.data.mappers.ExchangeRateTimeMapper
import com.example.fantasystore.data.mappers.FrankConversionMapper
import com.example.fantasystore.data.repositories.FrankfurterRateRepository
import com.example.fantasystore.domain.ExchangeRateTime
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    private val frankfurterRateRepository = FrankfurterRateRepository(FrankConversionMapper, ExchangeRateTimeMapper)
    var isLoading by mutableStateOf(false)
        private set

    var exchangeRateTime by mutableStateOf<ExchangeRateTime?>(null)
        private set

    init {
        getHistoricalConversion("2024-01-01", "2024-05-31", "USD", "MXN")
    }

    fun getHistoricalConversion(startDate: String, endDate: String, from: String, to: String) {
        viewModelScope.launch {
            isLoading = true
            val result = frankfurterRateRepository.getHistoricalConversion(startDate, endDate, from, to)
            Log.d("result", "getHistoricalConversion: $result")
            exchangeRateTime = result
            isLoading = false
        }
    }

}