package com.baka3k.architecture.feature.movie.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.baka3k.architecture.core.ui.R
import com.baka3k.architecture.core.ui.compose.ShimmerAnimation
import com.baka3k.architecture.core.ui.compose.ShimmerList
import com.baka3k.architecture.core.ui.theme.AppTheme
import com.baka3k.architecture.feature.movie.PopularUiState
import com.baka3k.core.common.logger.Logger
import com.baka3k.core.data.movie.model.PhotoSize
import com.baka3k.core.data.movie.model.asPhotoUrl
import kotlin.random.Random


@Composable
fun popularMovieView(
    navigateToMovieDetail: (String) -> Unit,
    popularUiState: PopularUiState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(
                start = 15.dp,
                end = 15.dp,
                top = 15.dp,
                bottom = 10.dp
            )
            .fillMaxWidth()
            .fillMaxHeight()
            .shadow(
                elevation = 15.dp,
                shape = RoundedCornerShape(
                    topStart = 11.dp,
                    topEnd = 11.dp,
                    bottomEnd = 11.dp,
                    bottomStart = 11.dp
                ),
                clip = false,
                ambientColor = AppTheme.colors.colorBackgroundTheme,
                spotColor = Color.Black,
            ),
        elevation = CardDefaults.elevatedCardElevation(),
        shape = RoundedCornerShape(

            bottomEnd = 7.dp,
            bottomStart = 7.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.colorBackgroundTheme,
        ),
    ) {
        Column {
            Text(
                text = "Travel, Tours and Tracking",
                style = LocalTextStyle.current.copy(AppTheme.colors.colorContentEditText)
            )
            popularItem(
                navigateToMovieDetail = navigateToMovieDetail,
                popularUiState = popularUiState,
                modifier = modifier
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun popularItem(
    navigateToMovieDetail: (String) -> Unit,
    popularUiState: PopularUiState,
    modifier: Modifier = Modifier
) {

    when (popularUiState) {
        PopularUiState.Loading -> {
//            ShimmerList()
            ShimmerAnimation()
        }
        PopularUiState.Error -> {
            Text(text = "$popularUiState")
        }
        is PopularUiState.Success -> {
            LazyHorizontalStaggeredGrid(rows = StaggeredGridCells.Adaptive(100.dp)) {
                items(popularUiState.movies) { movie ->
                    Box(modifier = modifier
                        .fillMaxWidth()
                        .aspectRatio(
                            ratio = Random
                                .nextDouble(0.7, 1.8)
                                .toFloat()
                        )
                        .clickable {
                            Logger.d("#popularItem() ${movie.title}")
                            navigateToMovieDetail(movie.id.toString())
                        },
                        content = {
                            AsyncImage(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .background(Color.Blue),
                                contentScale = ContentScale.Crop,
                                model = ImageRequest.Builder(LocalContext.current)
                                    .placeholder(R.drawable.placeholder)
                                    .data(movie.backdropPath.asPhotoUrl(PhotoSize.BackDrop.w300))
                                    .diskCachePolicy(CachePolicy.ENABLED).build(),
                                contentDescription = movie.title,
                            )
                        }
                    )
                }
            }
        }
    }
}
