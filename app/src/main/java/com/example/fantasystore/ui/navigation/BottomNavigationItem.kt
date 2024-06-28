package com.example.fantasystore.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.fantasystore.R

sealed class BottomNavigationItem(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    data object Home : BottomNavigationItem("home", R.string.home_screen_route, Icons.Filled.Home)
    data object History : BottomNavigationItem("history", R.string.history_screen_route, Icons.Filled.StackedLineChart)
}

