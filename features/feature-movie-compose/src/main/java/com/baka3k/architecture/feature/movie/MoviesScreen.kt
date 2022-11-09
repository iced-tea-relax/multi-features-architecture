package com.baka3k.architecture.feature.movie

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.baka3k.architecture.core.ui.theme.AppTheme
import com.baka3k.architecture.feature.movie.ui.nowPlayingMovie
import com.baka3k.architecture.feature.movie.ui.popularMovieView


@Composable
fun MovieRoute(
    navigateToMovieDetail: (String) -> Unit,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val uiNowPlayingUiState by viewModel.nowPlayingUiState.collectAsState()
    val uiPopularUiState by viewModel.popularUiState.collectAsState()
    val popularLoadMoreState = rememberLazyListState()
    MoviesScreen(
        navigateToMovieDetail = navigateToMovieDetail,
        nowPlayingUiState = uiNowPlayingUiState,
        popularUiState = uiPopularUiState
    )
    popularLoadMoreState.OnBottomReached {
        viewModel.loadMorePopular()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun customEditText(modifier: Modifier = Modifier, onValueChanged: (String) -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp)
    )
    {
        var value by rememberSaveable {
            mutableStateOf("")
        }
        val transformation = VisualTransformation.None
        val interactionSource = remember { MutableInteractionSource() }
        BasicTextField(
            value = value,
            enabled = true,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodySmall.copy(color = AppTheme.colors.colorContentEditText),
            decorationBox = { innerTextField ->
                Row(
                    Modifier
                        .height(44.dp)
                        .fillMaxWidth()
                        .background(shape = CircleShape, color = AppTheme.colors.colorBgEditText)
                        .align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "",
                        modifier = Modifier.padding(start = 5.dp),
                        tint = AppTheme.colors.colorContentEditText
                    )
                    TextFieldDefaults.TextFieldDecorationBox(
                        value = value,
                        innerTextField = innerTextField,
                        enabled = true,
                        singleLine = true,
                        visualTransformation = transformation,
                        interactionSource = interactionSource,
                        contentPadding = PaddingValues(start = 5.dp, end = 20.dp),
                        placeholder = {
                            Text(
                                "Search",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    )
                }

            },
            onValueChange = {
                value = it
                onValueChanged.invoke(it)
            },
        )
    }
}

@Composable
fun MoviesScreen(
    navigateToMovieDetail: (String) -> Unit,
    nowPlayingUiState: NowPlayingUiState,
    popularUiState: PopularUiState
) {
    val reusableModifier = Modifier.fillMaxWidth()
    Column {
        customEditText {}
        nowPlayingMovie(
            navigateToMovieDetail = navigateToMovieDetail,
            nowPlayingUiState = nowPlayingUiState,
            modifier = reusableModifier
        )
        popularMovieView(
            navigateToMovieDetail = navigateToMovieDetail,
            popularUiState = popularUiState,
            modifier = reusableModifier
        )
    }
}

@Composable
fun LazyListState.OnBottomReached(
    loadMore: () -> Unit
) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf true

            lastVisibleItem.index == layoutInfo.totalItemsCount - 1
        }
    }
    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .collect {
                if (it) loadMore()
            }
    }
}