package com.baka3k.architecture.feature.movie.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.baka3k.architecture.core.ui.compose.ShimmerAnimation
import com.baka3k.architecture.core.ui.theme.AppTheme
import com.baka3k.architecture.feature.movie.NowPlayingUiState
import com.baka3k.core.common.logger.Logger
import com.baka3k.core.data.movie.model.PhotoSize
import com.baka3k.core.data.movie.model.asPhotoUrl
import com.baka3k.core.model.Movie
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager

@Composable
fun nowPlayingMovie(
    navigateToMovieDetail: (String) -> Unit,
    nowPlayingUiState: NowPlayingUiState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(AppTheme.colors.colorBackgroundTheme)
    ) {
        when (nowPlayingUiState) {
            NowPlayingUiState.Loading -> {
                loading(modifier.align(Alignment.Center))
            }
            NowPlayingUiState.Error -> {
                error(nowPlayingUiState)
            }
            is NowPlayingUiState.Success -> {
                horizontalList(
                    nowPlayingUiState = nowPlayingUiState,
                    navigateToMovieDetail = navigateToMovieDetail
                )
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun horizontalList(
    navigateToMovieDetail: (String) -> Unit,
    nowPlayingUiState: NowPlayingUiState.Success,
    modifier: Modifier = Modifier
) {
    HorizontalPager(
        count = nowPlayingUiState.movies.size,
        contentPadding = PaddingValues(start = 25.dp, end = 25.dp),
    ) { page ->
        page(movie = nowPlayingUiState.movies[page], modifier = modifier.clickable {
            val movie = nowPlayingUiState.movies[page]
            Logger.d("#page() ${movie.title}")
            navigateToMovieDetail(movie.id.toString())
        })
    }
}

@Composable
private fun page(movie: Movie, modifier: Modifier = Modifier) {
    val topColor = AppTheme.colors.colorBackgroundTheme
    val colorGray = Color(220, 220, 220, 0)
    val gradientGrayWhite = Brush.verticalGradient(0f to topColor, 40f to colorGray)
    Card(
        shape = RoundedCornerShape(1.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.colorBackgroundTheme),
    ) {
        ConstraintLayout {
            val (topLayout, photoLayout) = createRefs()
            photo(movie, modifier = modifier
                .constrainAs(photoLayout) {

                }
            )
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(85.dp)
                    .padding(start = 10.dp, end = 10.dp)
                    .background(
                        gradientGrayWhite
                    )
                    .constrainAs(topLayout) {
                        top.linkTo(parent.top, margin = 0.dp)
                    },
            ) {
                Text(
                    movie.title,
                    style = MaterialTheme.typography.headlineSmall.copy(color = AppTheme.colors.colorTextHeaderHome),
                    modifier = modifier.align(Alignment.CenterStart)
                )
            }
        }
    }
}

@Composable
private fun error(nowPlayingUiState: NowPlayingUiState, modifier: Modifier = Modifier) {
    Text(text = "$nowPlayingUiState")
}

@Composable
private fun loading(modifier: Modifier = Modifier) {
//    CircularProgressIndicator(modifier)
    ShimmerAnimation()
}

@Composable
private fun photo(it: Movie, modifier: Modifier = Modifier) {
    AsyncImage(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp)
            .aspectRatio(0.8f)
            .background(Color.Blue),
        contentScale = ContentScale.Crop,
        model = ImageRequest.Builder(LocalContext.current)
            .placeholder(com.baka3k.architecture.core.ui.R.drawable.placeholder)
            .data(it.posterPath.asPhotoUrl(PhotoSize.Poster.w500))
            .diskCachePolicy(CachePolicy.ENABLED).build(),
        contentDescription = it.title,
    )
}



