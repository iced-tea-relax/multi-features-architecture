package com.baka3k.architecture.multifeatures.navigation

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.baka3k.architecture.feature.movie.navigation.MovieDestination
import com.baka3k.architecture.feature.movie.navigation.movieScreenComposeGraph
import com.baka3k.test.movie.detail.navigation.MovieDetailDestination
import com.baka3k.test.movie.detail.navigation.movieDetailComposeGraph

@Composable
fun HiNavHost(
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = MovieDestination.startScreen,
    onBackPress: () -> Unit
) {
    NavHost(navController = navController, startDestination = startDestination, modifier = modifier)
    {
        movieScreenComposeGraph(
            navigateToMovieDetail = {
//                navController.navigate(MovieDetailDestination.createNavigationRoute(it.toString()))
                navController.navigate(MovieDetailDestination.createNavigationRouteByDeepLink(it))
            }
        )
        movieDetailComposeGraph(onBackPress = onBackPress)
    }
}


