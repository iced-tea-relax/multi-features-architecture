package com.baka3k.architecture.feature.movie.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.baka3k.architecture.feature.movie.MovieRoute
import com.baka3k.core.navigation.Screen

object MovieDestination : Screen {
    override val startScreen: String
        get() = "movie_router"
    override val destinationScreen: String
        get() = "movie_destination"
    override val deepLinkUrl: String
        get() = "android-app://com.baka3k.test.feature.movie.router/MoviesScreen"
}

fun NavGraphBuilder.movieScreenComposeGraph(
    navigateToMovieDetail: (String) -> Unit
) {
    composable(
        route = MovieDestination.startScreen
    ) {
        MovieRoute(navigateToMovieDetail = navigateToMovieDetail)
    }
}
