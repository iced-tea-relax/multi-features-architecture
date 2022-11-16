package com.baka3k.architecture.multifeatures

import android.os.Bundle
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.viewbinding.ViewBinding
import com.baka3k.architecture.core.ui.BaseActivity
import com.baka3k.architecture.multifeatures.databinding.ActivityMainBinding
import com.baka3k.test.feature.movie.router.graphScreenMovieList
import com.baka3k.test.feature.moviedetail.router.MovieDetailScreenRouter
import com.baka3k.test.feature.moviedetail.router.graphScreenMovieDetail
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun initViewBinding(): ViewBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        setPrimaryNavigationFragment(finalHost) // equivalent to app:defaultNavHost="true"

        val navigationController = findNavController(R.id.nav_host_fragment_content_main)
        if (navigationController != null) {
            navigationController.graph = navigationController.graphScreenMovieList(
                navigateMovieDetail = { navigationController.navigate("${MovieDetailScreenRouter.deepLinkUrl}?$it".toUri()) },
                navigateToActor = {}
            ).apply {
                id = 100
            }
            navigationController.graph.addDestination(
                navigationController.graphScreenMovieDetail().apply { id = 101 })
        }
    }
}