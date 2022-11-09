package com.baka3k.architecture.multifeatures.navigation

import android.util.Log
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.baka3k.architecture.feature.movie.navigation.MovieDestination
import com.baka3k.architecture.feature.movie.navigation.movieScreenComposeGraph
import com.baka3k.test.feature.moviedetail.router.MovieDetailScreenRouter
import com.baka3k.test.movie.detail.navigation.MovieDetailDestination
import com.baka3k.test.movie.detail.navigation.movieDetailComposeGraph

@Composable
fun HiNavHost(
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = MovieDestination.startScreen
) {
    NavHost(navController = navController, startDestination = startDestination, modifier = modifier)
    {
        movieScreenComposeGraph(
            navigateToMovieDetail = {
//                navController.navigate(MovieDetailDestination.createNavigationRoute(it))
                navController.navigate(MovieDetailDestination.createNavigationRouteByDeepLink(it))
            }
        )
        movieDetailComposeGraph()
    }
//    NavHost(
//        navController = navController,
//        startDestination = startDestination,
//        modifier = modifier,
//    ) {
//        movieScreenGraph(
//            windowSizeClass = windowSizeClass
//        )
////        interestsGraph(
//            navigateToTopic = { navController.navigate("${TopicDestination.route}/$it") },
////            navigateToAuthor = { navController.navigate("${AuthorDestination.route}/$it") },
////            nestedGraphs = {
////                topicGraph(onBackClick = { navController.popBackStack() })
////                authorGraph(onBackClick = { navController.popBackStack() })
////            }
////        )
//    }
}


