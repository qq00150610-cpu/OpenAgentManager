package com.openagent.manager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.openagent.manager.ui.dashboard.DashboardScreen
import com.openagent.manager.ui.agents.AgentsScreen
import com.openagent.manager.ui.editor.EditorScreen
import com.openagent.manager.ui.settings.SettingsScreen
import kotlinx.serialization.Serializable

// ── 路由定义 ─────────────────────────────

sealed class Screen {
    @Serializable data object Dashboard
    @Serializable data object Agents
    @Serializable data object Editor
    @Serializable data object Settings
}

// ── 导航图 ───────────────────────────────

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard,
        modifier = modifier
    ) {
        composable<Screen.Dashboard> { DashboardScreen() }
        composable<Screen.Agents> { AgentsScreen() }
        composable<Screen.Editor> { EditorScreen() }
        composable<Screen.Settings> { SettingsScreen() }
    }
}
