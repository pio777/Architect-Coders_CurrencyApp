package com.example.fantasystore.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fantasystore.ui.history.HistoryScreen
import com.example.fantasystore.ui.home.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = BottomNavigationItem.Home.route) {
        composable(BottomNavigationItem.Home.route) { HomeScreen(navController) }
        composable(BottomNavigationItem.History.route) { HistoryScreen(navController) }
    }
}