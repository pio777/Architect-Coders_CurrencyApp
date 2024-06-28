package com.example.fantasystore.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fantasystore.ui.history.HistoryScreen
import com.example.fantasystore.ui.home.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val customEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? = {
        slideInHorizontally(
            initialOffsetX = { 300 }, // Ajusta el offset para evitar superposición con el Bottom Navigation
            animationSpec = tween(durationMillis = 300, easing = LinearEasing)
        )
    }

    val customExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition? = {
        slideOutHorizontally(
            targetOffsetX = { -300 }, // Ajusta el offset para evitar superposición con el Bottom Navigation
            animationSpec = tween(durationMillis = 300, easing = LinearEasing)
        )
    }
    NavHost(navController = navController, startDestination = BottomNavigationItem.Home.route) {
//        composable(
//            BottomNavigationItem.Home.route,
//            /* enterTransition = customEnterTransition,
             exitTransition = customExitTransition*/
        ) { HomeScreen(navController) }
        composable(
            BottomNavigationItem.History.route,
            /*enterTransition = { slideInHorizontally(initialOffsetX = { 300 }) }, // Entra desde la derecha
            exitTransition = { slideOutHorizontally(targetOffsetX = { -150 }) } // Sale hacia la derecha*/
        ) { HistoryScreen(navController) }
    }
}