package com.example.currencyconverter.ui.common

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.enterAlwaysScrollBehavior
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.currencyconverter.R
import com.example.currencyconverter.ui.navigation.BottomNavigationItem
import com.example.currencyconverter.ui.theme.CurrencyConverterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenContent(
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior: TopAppBarScrollBehavior = enterAlwaysScrollBehavior()
    val bottomNavigationItems = listOf(
        BottomNavigationItem.Home,
        BottomNavigationItem.History
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = { AppBottomNavigation(navController, bottomNavigationItems) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets.safeDrawing,
        content = content
    )
}

@Composable
fun AppBottomNavigation(navController: NavHostController, items: List<BottomNavigationItem>) {
    val context = LocalContext.current
    val window = (context as Activity).window

    SideEffect { window.navigationBarColor = Color.Transparent.toArgb() }

    NavigationBar(modifier = Modifier.height(120.dp)) {
        val currentRoute = getCurrentRoute(navController)
        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            // Evita crear múltiples instancias de la misma pantalla en la pila de navegación
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            // Evita agregar la misma ruta a la pila de navegación si ya está en la parte superiorlaunchSingleTop = true
                            // Restaura el estado de la pantalla si se vuelve a navegar a ella
                            restoreState = true
                        }
                    }
                },
                icon = { BottomNavigationIcon(screen) },
                label = { Text(text = stringResource(id = screen.resourceId)) }
            )
        }
    }
}

@Composable
fun BottomNavigationIcon(screen: BottomNavigationItem) {
    Image(imageVector = screen.icon, contentDescription = stringResource(id = screen.resourceId))
}

@Composable
private fun getCurrentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@Preview(showBackground = true, device = "id:pixel_xl")
@Composable
fun AppBottomNavigationPreview() {
    val navController = rememberNavController()
    val bottomNavigationItems = listOf(
        BottomNavigationItem.Home,
        BottomNavigationItem.History
    )
    CurrencyConverterTheme { AppBottomNavigation(navController, bottomNavigationItems) }
}
