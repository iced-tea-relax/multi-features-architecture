package com.baka3k.architecture.multifeatures

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Stream
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.baka3k.architecture.core.ui.component.HiBackground
import com.baka3k.architecture.core.ui.theme.AppTheme
import com.baka3k.architecture.multifeatures.navigation.HiNavHost
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainComposeActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContent {
//            MovieRoute()
//            val navController = rememberNavController()
////            val niaTopLevelNavigation = remember(navController) {
////                NiaTopLevelNavigation(navController)
////            }
//            MaterialTheme {
//                HiNavHost(
//                    windowSizeClass = calculateWindowSizeClass(this),
//                    navController = navController,
//                    modifier = Modifier
//                        .padding(0.dp)
//                )
//            }
            val windowSizeClass = calculateWindowSizeClass(MainComposeActivity@ this)
            HiComposeApp(windowSizeClass)
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun HiComposeApp(windowSizeClass: WindowSizeClass) {
    AppTheme(
        darkTheme = ThemeState.isDarkTheme,
    ) {
        HiBackground {
            val navController = rememberNavController()
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .semantics {
                        testTagsAsResourceId = true
                    },
                containerColor = Color.Transparent,
                contentColor = AppTheme.colors.colorBackgroundTheme,
                bottomBar = { BottomBarNavigation(navController) }
            ) { padding ->
                Column {
                    HiNavHost(
                        windowSizeClass = windowSizeClass,
                        navController = navController,
                        modifier = Modifier
                            .padding(padding)
                            .consumedWindowInsets(padding)
                    )
                }

            }
        }
    }
}

@Composable
fun BottomBarNavigation(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Music,
        NavigationItem.Movies,
        NavigationItem.Profile
    )
    Surface(color = AppTheme.colors.colorBackgroundTheme) {
        NavigationBar(containerColor = Color.Transparent) {
            items.forEach { destination ->
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        ThemeState.isDarkTheme = !ThemeState.isDarkTheme
                    },
                    icon = {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = "",
                            modifier = Modifier.padding(start = 5.dp),
                            tint = AppTheme.colors.colorContentEditText
                        )
                    },
                    label = {
                        Text(
                            destination.title,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                        )
                    }
                )
            }
        }
    }

}

sealed class NavigationItem(var route: String, var icon: ImageVector, var title: String) {
    object Home : NavigationItem("home", Icons.Default.Home, "Home")
    object Music : NavigationItem("music", Icons.Default.Stream, "Streams")
    object Movies : NavigationItem("movies", Icons.Default.Message, "Message")
    object Profile : NavigationItem("profile", Icons.Default.AccountCircle, "Profile")
}

object ThemeState {
    var isDarkTheme by mutableStateOf(true)
}
