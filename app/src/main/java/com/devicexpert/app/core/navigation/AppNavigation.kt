package com.devicexpert.app.core.navigation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.devicexpert.app.presentation.dashboard.DashboardScreen
import com.devicexpert.app.presentation.livemonitor.LiveMonitorScreen
import com.devicexpert.app.presentation.analytics.AnalyticsScreen
import com.devicexpert.app.presentation.alerts.AlertsScreen
@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = Route.Dashboard, modifier = modifier) {
        composable<Route.Dashboard> {
            DashboardScreen(onNavigateToLiveMonitor = { deviceId -> navController.navigate(Route.LiveMonitor(deviceId)) })
        }
        composable<Route.LiveMonitor> {
            LiveMonitorScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable<Route.Analytics> {
            AnalyticsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable<Route.Alerts> {
            AlertsScreen()
        }
    }
}