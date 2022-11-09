package com.baka3k.test.feature.moviedetail.router

import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.createGraph
import androidx.navigation.fragment.fragment
import com.baka3k.core.navigation.Screen
import com.baka3k.core.navigation.toUriWithParams
import com.baka3k.test.feature.moviedetail.MovieDetailFragment

object MovieDetailScreenRouter : Screen {
    override val startScreen: String
        get() = "movie_detail_router"
    override val destinationScreen: String
        get() = "movie_detail_destination"
    override val deepLinkUrl: String
        get() = "android-app://com.baka3k.test.feature.moviedetail.router/moviedetailfragment"
}


inline fun NavController.graphScreenMovieDetail(): NavGraph {
    return createGraph(
        startDestination = MovieDetailScreenRouter.startScreen,
    ) {
        fragment<MovieDetailFragment>(MovieDetailScreenRouter.startScreen) {
            label = "movie Detail"
            deepLink {
                uriPattern =
                    "${MovieDetailScreenRouter.deepLinkUrl}?idmovie={idmovie}&name={name}&poster={poster}"
            }
        }
    }
}
inline fun NavController.navigateMovieDetailScreen(params: Map<String, String>?) {
    var uri = MovieDetailScreenRouter.deepLinkUrl.toUriWithParams(params)
    navigate(uri)
}
inline fun NavController.navigateMovieDetailScreen(idmovie: String, name: String, poster: String) {
    val params = mapOf(
        "idmovie" to idmovie,
        "name" to name,
        "poster" to poster
    )
    navigateMovieDetailScreen(params)
}