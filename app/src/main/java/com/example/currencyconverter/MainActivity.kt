package com.example.currencyconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.currencyconverter.ui.navigation.AppNavigation
//import com.google.firebase.crashlytics.FirebaseCrashlytics

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Full screen app theme
       /* FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true) // Enable crash reporting
        FirebaseCrashlytics.getInstance().log("About to crash!")
        throw RuntimeException("Test Crash") // Force a crash*/

        setContent { AppNavigation() }
    }
}