package com.baka3k.test.movie.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.baka3k.architecture.core.ui.component.HiTopAppBar
import com.baka3k.architecture.core.ui.component.ShimmerAnimation
import com.baka3k.architecture.core.ui.theme.AppTheme
import com.baka3k.test.movie.detail.ui.MovieInfoScreen
import com.baka3k.test.movie.detail.ui.castScreen
import com.baka3k.test.movie.detail.ui.crewScreen

@Composable
fun MovieDetailRouter(
    viewModel: MovieDetailViewModel = hiltViewModel(),
    onBackPress: () -> Unit
) {
    val movieDetailUiState by viewModel.movieDetailUiState.collectAsState()
    val creditUiState by viewModel.creditUiState.collectAsState()
    MovieDetailScreen(
        movieDetailUiState = movieDetailUiState,
        creditUiState = creditUiState,
        onBackPress = onBackPress
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    movieDetailUiState: MovieDetailUiState, creditUiState: CreditUiState,
    onBackPress: () -> Unit
) {
    val reusableModifier = Modifier.fillMaxWidth()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Box(modifier = reusableModifier)
    {
        Column(
            modifier = reusableModifier
                .background(AppTheme.colors.colorBackgroundTheme)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
        ) {
            movieInforScreen(movieDetailUiState = movieDetailUiState, modifier = reusableModifier)
            title(text = "Cast", modifier = reusableModifier)
            castScreen(castUiState = creditUiState.castUiState, modifier = reusableModifier)
            title(text = "Crew", modifier = reusableModifier)
            crewScreen(crewUiState = creditUiState.crewUiState, modifier = reusableModifier)
        }
        HiTopAppBar(
            title = "",
            onBackPress = onBackPress,
            modifier = Modifier.wrapContentHeight(),
            scrollBehavior = scrollBehavior
        )
    }
}

@Composable
fun title(text: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier.padding(top = 11.dp, start = 7.dp),
        text = text,
        style = LocalTextStyle.current.copy(Color(106, 82, 156))
    )
}

@Composable
private fun movieInforScreen(
    movieDetailUiState: MovieDetailUiState,
    modifier: Modifier
) {
    when (movieDetailUiState) {
        MovieDetailUiState.Loading -> {
            ShimmerAnimation()
        }
        MovieDetailUiState.Error -> {
            ShimmerAnimation()
        }
        is MovieDetailUiState.Success -> {
            val movie = movieDetailUiState.movie
            MovieInfoScreen(movie = movie, modifier = modifier)
        }
    }
}