package com.baka3k.test.feature.movie.ui.movielist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baka3k.core.common.result.Result
import com.baka3k.core.model.Movie
import com.baka3k.test.feature.movie.interactor.GetMovieNowPlayingUseCase
import com.baka3k.test.feature.movie.interactor.GetMoviePopularUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val moviePopularUseCase: GetMoviePopularUseCase,
    private val movieNowPlayingUseCase: GetMovieNowPlayingUseCase
) : ViewModel() {
    private val _isLoadingNowPlaying = MutableStateFlow(false)
    private val _isLoadingPolular = MutableStateFlow(false)
    val nowPlayingUiState =
        movieNowPlayingUseCase.invoke()
            .combine(_isLoadingNowPlaying) { nowPlayingResult, isLoading ->
                if (isLoading) {
                    NowPlayingUiState.Loading
                } else {
                    val nowplayingState: NowPlayingUiState = when (nowPlayingResult) {
                        is Result.Success -> {
                            NowPlayingUiState.Success(nowPlayingResult.data.map(Movie::toMovieState))
                        }
                        is Result.Loading -> NowPlayingUiState.Loading
                        is Result.Error -> NowPlayingUiState.Error
                    }
                    nowplayingState
                }

            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = NowPlayingUiState.Loading
            )
    val popularUiState =
        moviePopularUseCase.invoke()
            .combine(_isLoadingPolular) { popularMoviesResult, isLoading ->
                if (isLoading) {
                    PopularUiState.Loading
                } else {
                    val popularMovieState: PopularUiState = when (popularMoviesResult) {
                        is Result.Success -> {
                            PopularUiState.Success(popularMoviesResult.data.map(Movie::toMovieState))
                        }
                        is Result.Loading -> PopularUiState.Loading
                        is Result.Error -> PopularUiState.Error
                    }
                    popularMovieState
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PopularUiState.Loading
            )

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                _isLoadingNowPlaying.value = true
                movieNowPlayingUseCase.loadMore()
                _isLoadingNowPlaying.value = false
            }

            launch {
                _isLoadingPolular.value = true
                moviePopularUseCase.loadMore()
                _isLoadingPolular.value = false
            }
        }
    }

    fun loadMoreNowPlaying() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoadingNowPlaying.value = true
            movieNowPlayingUseCase.loadMore()
            _isLoadingNowPlaying.value = false
        }
    }

    fun loadMorePopular() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoadingPolular.value = true
            moviePopularUseCase.loadMore()
            _isLoadingPolular.value = false
        }
    }
}