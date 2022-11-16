package com.baka3k.test.feature.moviedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baka3k.core.common.result.Result
import com.baka3k.core.model.Movie
import com.baka3k.test.feature.moviedetail.interactor.GetMovieDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    movieDetailUseCase: GetMovieDetailUseCase
) : ViewModel() {
    private val movieId: Long = checkNotNull(savedStateHandle["idmovie"])
    val movieDetailUIState = movieDetailUseCase.invoke(movieId).map {
        when (it) {
            is Result.Success -> {
                MovieDetailUiState.Success(it.data)
            }
            is Result.Loading -> {
                MovieDetailUiState.Loading
            }
            else -> {
                MovieDetailUiState.Error
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MovieDetailUiState.Loading
    )
}

sealed interface MovieDetailUiState {
    data class Success(val movie: Movie) : MovieDetailUiState
    object Error : MovieDetailUiState
    object Loading : MovieDetailUiState
}
