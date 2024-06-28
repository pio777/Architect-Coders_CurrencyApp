package com.example.fantasystore.ui.home

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fantasystore.data.FavoriteCurrenciesManager
import com.example.fantasystore.data.mappers.ExchangeHistoryMapper
import com.example.fantasystore.data.mappers.ExchangeRateMapper
import com.example.fantasystore.data.mappers.ExchangeRateTimeMapper
import com.example.fantasystore.data.mappers.FrankConversionMapper
import com.example.fantasystore.data.repositories.ExchangeRateRepository
import com.example.fantasystore.data.repositories.FrankfurterRateRepository
import com.example.fantasystore.domain.Currency
import com.example.fantasystore.domain.currenciesList
import com.example.fantasystore.domain.topWorldCurrencies
import com.example.fantasystore.domain.usecases.GetConversionStrategyUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

private const val EMPTY_STRING = ""

@OptIn(FlowPreview::class)
class HomeViewModel(context: Context) : ViewModel() {
    private val frankfurterRateRepository = FrankfurterRateRepository(FrankConversionMapper, ExchangeRateTimeMapper)
    private val exchangeRateRepository = ExchangeRateRepository(ExchangeRateMapper, ExchangeHistoryMapper)
    private val getConversionStrategyUseCase = GetConversionStrategyUseCase(frankfurterRateRepository, exchangeRateRepository)
    private val favoriteCurrenciesManager = FavoriteCurrenciesManager(context)

    private val _favoriteCurrencies = MutableStateFlow<List<Pair<Currency, Currency>>>(emptyList())
    val favoriteCurrencies: StateFlow<List<Pair<Currency, Currency>>> = _favoriteCurrencies.asStateFlow()
    private val _result = MutableStateFlow<Double?>(null)
    val result: StateFlow<Double?> = _result.asStateFlow()

    private val currencies: List<Currency> = topWorldCurrencies
    var isLoading by mutableStateOf(false)
        private set

    data class CurrencyInput(
        val currency: MutableStateFlow<Currency?> = MutableStateFlow(null),
        val amount: MutableStateFlow<String> = MutableStateFlow(EMPTY_STRING)
    )

    val input1 = CurrencyInput()
    val input2 = CurrencyInput()

    enum class UpdateSource { INPUT1, INPUT2, NONE }

    var updateSource by mutableStateOf(UpdateSource.NONE)

    private val conversionDelay = 500L

    init {
        viewModelScope.launch { loadFavoriteCurrencies() }

        viewModelScope.launch {
            combine(
                input1.currency,
                input2.currency,
                input1.amount.debounce(conversionDelay).distinctUntilChanged(),
                input2.amount.debounce(conversionDelay).distinctUntilChanged()
            ) { currency1, currency2, amount1, amount2 ->
                Log.d(
                    "HomeViewModel",
                    "Currency 1: ${currency1?.code}, Currency 2: ${currency2?.code}, Amount 1: $amount1, Amount 2: $amount2"
                )

                if (currency1 != null && currency2 != null && (amount1.isNotEmpty() || amount2.isNotEmpty())) {
                    performConversion()
                }
            }.collect {}
        }
    }

    private suspend fun performConversion() {
        val amount1 = input1.amount.value
        val amount2 = input2.amount.value
        val currency1 = input1.currency.value
        val currency2 = input2.currency.value

        if (currency1 != null && currency2 != null) {
            when (updateSource) {
                UpdateSource.INPUT1 -> {
                    if (amount1.isNotEmpty()) {

                        isLoading = true
                        val result = getConversionStrategyUseCase(currency1, currency2).convert(amount1.toDouble(), currency1, currency2)
                        _result.value = result
                        Log.d("HomeViewModel", "Updated _result.value to input 1 $result")
                        input2.amount.value = result.toString()
                        isLoading = false
                    }
                }

                UpdateSource.INPUT2 -> {
                    if (amount2.isNotEmpty()) {
                        isLoading = true
                        val result = getConversionStrategyUseCase(currency2, currency1).convert(amount2.toDouble(), currency2, currency1)
                        _result.value = result
                        Log.d("HomeViewModel", "Updated _result.value to input 2 $result")
                        input1.amount.value = result.toString()
                        isLoading = false
                    }
                }

                UpdateSource.NONE -> Unit
            }
        }
        updateSource = UpdateSource.NONE
    }

    fun onAmountChange(inputIndex: Int, newAmount: String) {
        viewModelScope.launch {
            updateSource = if (inputIndex == 0) UpdateSource.INPUT1 else UpdateSource.INPUT2

            if (newAmount.isEmpty()) {
                // Clear both amounts and reset the result
                input1.amount.value = EMPTY_STRING
                input2.amount.value = EMPTY_STRING
                _result.value = null
            } else {
                // Update the corresponding amount
                if (inputIndex == 0) {
                    input1.amount.value = newAmount
                } else {
                    input2.amount.value = newAmount
                }
            }
        }
    }

    fun onCurrencySelected(input: CurrencyInput, currency: Currency) {
        input.currency.value = currency
    }

    fun getAvailableCurrencies(inputIndex: Int): List<Currency> {
        val selectedCurrency = if (inputIndex == 0) input2.currency.value else input1.currency.value
        return currencies.filter { it != selectedCurrency }
    }

    fun addFavoritePair() {
        viewModelScope.launch {
            val currency1 = input1.currency.value?.code ?: return@launch
            val currency2 = input2.currency.value?.code ?: return@launch
            favoriteCurrenciesManager.saveFavoritePair(currency1, currency2)
            loadFavoriteCurrencies()
            Log.d("HomeViewModel", "Favoritos actualizados: ${_favoriteCurrencies.value}")
        }
    }

    private suspend fun loadFavoriteCurrencies() {
        favoriteCurrenciesManager.getFavoritePair().collect { (currency1Code, currency2Code) ->
            if (currency1Code != null && currency2Code != null) {
                val currency1 = currenciesList.find { it.code == currency1Code }
                val currency2 = currenciesList.find { it.code == currency2Code }
                if (currency1 != null && currency2 != null) {
                    _favoriteCurrencies.value = listOf(Pair(currency1, currency2))
                    input1.currency.value = currency1
                    input2.currency.value = currency2
                }
            }
        }
    }
}


/*   private val exchangeRateRepository = ExchangeRateRepository(ExchangeRateMapper(RatingDataToDomainMapper()))
   var state by mutableStateOf(UiState()) // remember is not necessary in viewmodel because this stay alive at changes
       private set // this can be observable by UI (HomeScreen)

   fun onUiReady() {
       viewModelScope.launch {
           state = UiState(isLoading = true)
           val products = exchangeRateRepository.getProducts()
           state = UiState(products = products)
       }
   }

   data class UiState(
       val isLoading: Boolean = false,
       val products: List<Product> = emptyList(),
       val error: String? = null
   )*/