package com.example.currencyconverter.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteCurrenciesManager(private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "favorite_currencies")

    companion object {
        val CURRENCY_1_KEY = stringPreferencesKey("currency1")
        val CURRENCY_2_KEY = stringPreferencesKey("currency2")
    }

    suspend fun saveFavoritePair(currency1: String, currency2: String) {
        context.dataStore.edit { preferences ->
            preferences[CURRENCY_1_KEY] = currency1
            preferences[CURRENCY_2_KEY] = currency2
        }
    }

    fun getFavoritePair(): Flow<Pair<String?, String?>> {
        return context.dataStore.data.map { preferences ->
            Pair(preferences[CURRENCY_1_KEY], preferences[CURRENCY_2_KEY])
        }
    }
}