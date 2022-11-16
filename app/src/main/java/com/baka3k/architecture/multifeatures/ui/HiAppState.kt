package com.baka3k.architecture.multifeatures.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Stream
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Message
import androidx.compose.material.icons.rounded.Stream
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.DisposableEffectResult
import androidx.compose.runtime.DisposableEffectScope
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.util.trace
import androidx.metrics.performance.PerformanceMetricsState
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.baka3k.architecture.core.ui.theme.Icon
import com.baka3k.architecture.feature.movie.navigation.MovieDestination
import com.baka3k.architecture.multifeatures.R
import com.baka3k.architecture.multifeatures.navigation.TopLevelDestination
import com.baka3k.core.navigation.Screen

@Composable
fun rememberHiAppState(
    windowSizeClass: WindowSizeClass,
    navController: NavHostController = rememberNavController()
): HiAppState {
    NavigationTrackingSideEffect(navController)
    return remember(navController, windowSizeClass) {
        HiAppState(navController, windowSizeClass)
    }
}

@Stable
class HiAppState(
    val navController: NavHostController,
    private val windowSizeClass: WindowSizeClass
) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination
    val shouldShowBottomBar: Boolean
        get() = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact ||
                windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact

    val topLevelDestinations: List<TopLevelDestination> = listOf(
        TopLevelDestination(
            startScreen = MovieDestination.startScreen,
            destinationScreen = MovieDestination.destinationScreen,
            deepLinkUrl = MovieDestination.deepLinkUrl,
            selectedIcon = Icon.ImageVectorIcon(imageVector = Icons.Rounded.Home),
            unselectedIcon = Icon.ImageVectorIcon(imageVector = Icons.Default.Home),
            iconTextId = R.string.home
        ),
        TopLevelDestination(
            startScreen = MovieDestination.startScreen,
            destinationScreen = MovieDestination.destinationScreen,
            deepLinkUrl = MovieDestination.deepLinkUrl,
            selectedIcon = Icon.ImageVectorIcon(imageVector = Icons.Rounded.Stream),
            unselectedIcon = Icon.ImageVectorIcon(imageVector = Icons.Default.Stream),
            iconTextId = R.string.streams
        ),
        TopLevelDestination(
            startScreen = MovieDestination.startScreen,
            destinationScreen = MovieDestination.destinationScreen,
            deepLinkUrl = MovieDestination.deepLinkUrl,
            selectedIcon = Icon.ImageVectorIcon(imageVector = Icons.Rounded.Message),
            unselectedIcon = Icon.ImageVectorIcon(imageVector = Icons.Default.Message),
            iconTextId = R.string.message
        ),
        TopLevelDestination(
            startScreen = MovieDestination.startScreen,
            destinationScreen = MovieDestination.destinationScreen,
            deepLinkUrl = MovieDestination.deepLinkUrl,
            selectedIcon = Icon.ImageVectorIcon(imageVector = Icons.Rounded.AccountCircle),
            unselectedIcon = Icon.ImageVectorIcon(imageVector = Icons.Default.AccountCircle),
            iconTextId = R.string.profile
        ),
    )


    fun navigate(destination: Screen, route: String? = null) {
        trace("Navigation: $destination") {
            if (destination is TopLevelDestination) {
                navController.navigate(route ?: destination.startScreen) {
                    // Pop up to the start destination of the graph to
                    // avoid building up a large stack of destinations
                    // on the back stack as users select items
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    // Avoid multiple copies of the same destination when
                    // reselecting the same item
                    launchSingleTop = true
                    // Restore state when reselecting a previously selected item
                    restoreState = true
                }
            } else {
                navController.navigate(route ?: destination.startScreen)
            }
        }
    }

    fun onBackPress() {
        navController.popBackStack()
    }
}

/**
 * Stores information about navigation events to be used with JankStats
 */
@Composable
private fun NavigationTrackingSideEffect(navController: NavHostController) {
    JankMetricDisposableEffect(navController) { metricsHolder ->
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            metricsHolder.state?.putState("Navigation", destination.route.toString())
        }

        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }
}

@Composable
fun JankMetricDisposableEffect(
    vararg keys: Any?,
    reportMetric: DisposableEffectScope.(state: PerformanceMetricsState.Holder) -> DisposableEffectResult
) {
    val metrics = rememberMetricsStateHolder()
    DisposableEffect(metrics, *keys) {
        reportMetric(this, metrics)
    }
}

@Composable
fun rememberMetricsStateHolder(): PerformanceMetricsState.Holder {
    val localView = LocalView.current

    return remember(localView) {
        PerformanceMetricsState.getHolderForHierarchy(localView)
    }
}