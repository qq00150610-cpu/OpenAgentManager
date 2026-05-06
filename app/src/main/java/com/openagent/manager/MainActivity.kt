package com.openagent.manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.openagent.core.ui.theme.OpenAgentTheme
import com.openagent.manager.ui.navigation.AppNavGraph
import com.openagent.manager.ui.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OpenAgentTheme {
                MainScreen()
            }
        }
    }
}

// ── 底部导航项 ───────────────────────────

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: Any
)

// ── 主界面：TopBar + NavHost + BottomBar ──

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val items = listOf(
        BottomNavItem("仪表盘", Icons.Default.Dashboard, Screen.Dashboard),
        BottomNavItem("Agents", Icons.Default.Hub, Screen.Agents),
        BottomNavItem("编辑器", Icons.Default.Code, Screen.Editor),
        BottomNavItem("设置", Icons.Default.Settings, Screen.Settings)
    )

    val currentRoute = navBackStackEntry?.destination
    val currentTitle = items.find { item ->
        currentRoute?.hasRoute(item.route::class) == true
    }?.label ?: "OpenAgent"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(currentTitle) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute?.hasRoute(item.route::class) == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(Screen.Dashboard) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
