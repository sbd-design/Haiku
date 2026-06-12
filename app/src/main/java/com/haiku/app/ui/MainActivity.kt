package com.haiku.app.ui

import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.haiku.app.ui.screens.HomeScreen
import com.haiku.app.ui.screens.OnboardingScreen
import com.haiku.app.ui.screens.SettingsScreen
import com.haiku.app.ui.theme.HaikuTheme
import dagger.hilt.android.AndroidEntryPoint

private const val ROUTE_ONBOARDING = "onboarding"
private const val ROUTE_HOME = "home"
private const val ROUTE_SETTINGS = "settings"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var hasNotificationAccess by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HaikuTheme {
                val navController = rememberNavController()
                val startDest = if (hasNotificationAccess) ROUTE_HOME else ROUTE_ONBOARDING

                NavHost(navController = navController, startDestination = startDest) {
                    composable(ROUTE_ONBOARDING) {
                        OnboardingScreen(
                            hasNotificationAccess = hasNotificationAccess,
                            onComplete = {
                                navController.navigate(ROUTE_HOME) {
                                    popUpTo(ROUTE_ONBOARDING) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(ROUTE_HOME) {
                        HomeScreen(
                            onNavigateToSettings = { navController.navigate(ROUTE_SETTINGS) }
                        )
                    }
                    composable(ROUTE_SETTINGS) {
                        SettingsScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hasNotificationAccess = isNotificationListenerEnabled()
    }

    private fun isNotificationListenerEnabled(): Boolean {
        val enabled = Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners"
        ) ?: return false
        return enabled.contains(packageName)
    }
}
