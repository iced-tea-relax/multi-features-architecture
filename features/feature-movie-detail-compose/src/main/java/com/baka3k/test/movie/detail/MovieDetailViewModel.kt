package com.baka3k.test.movie.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baka3k.core.common.logger.Logger
import com.baka3k.core.common.result.Result
import com.baka3k.test.movie.detail.interactor.GetCastUseCase
import com.baka3k.test.movie.detail.interactor.GetCreditUseCase
import com.baka3k.test.movie.detail.interactor.GetCrewUseCase
import com.baka3k.test.movie.detail.interactor.GetGenreUseCase
import com.baka3k.test.movie.detail.interactor.GetMovieDetailUseCase
import com.baka3k.test.movie.detail.navigation.MovieDetailDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    movieDetailUseCase: GetMovieDetailUseCase,
    crewUseCase: GetCrewUseCase,
    castUseCase: GetCastUseCase,
    getGenreUseCase: GetGenreUseCase,
    private val getCreditUseCase: GetCreditUseCase

) : ViewModel() {
    private val _movieId: String = checkNotNull(
        savedStateHandle[MovieDetailDestination.movieIdArg]
    )
    private val movieId = _movieId.toLong()
    private val movieDetailStream = movieDetailUseCase.invoke(movieId).flowOn(Dispatchers.IO)
    private val genreStream = getGenreUseCase.invoke(movieId).flowOn(Dispatchers.IO)
    private val castStream = castUseCase.invoke(movieId).flowOn(Dispatchers.IO)
    private val crewStream = crewUseCase.invoke(movieId).flowOn(Dispatchers.IO)
    private val creditStream = loadCredit(movieId).flowOn(Dispatchers.IO)
    val creditUiState: StateFlow<CreditUiState> = combine(
        creditStream, castStream, crewStream
    ) { creditResult, castResult, crewResult ->
        val creditUiState = when (creditResult) {
            is Result.Loading -> {
                CreditUiState(crewUiState = CrewUiState.Loading, castUiState = CastUiState.Loading)
            }
            is Result.Error -> {
                Logger.d("test", "creditResult err $creditResult")
                CreditUiState(crewUiState = CrewUiState.Error, castUiState = CastUiState.Error)
            }
            else -> {
                val castData = if (castResult is Result.Success) {
                    castResult.data
                } else {
                    emptyList()
                }
                val crewData = if (crewResult is Result.Success) {
                    crewResult.data
                } else {
                    emptyList()
                }
                CreditUiState(
                    crewUiState = CrewUiState.Success(crewData),
                    castUiState = CastUiState.Success(castData)
                )
            }
        }
        creditUiState
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CreditUiState(
            crewUiState = CrewUiState.Loading, castUiState = CastUiState.Loading
        )
    )
    val movieDetailUiState =
        combine(movieDetailStream, genreStream) { movieDetailResult, genreResult ->
            val genreList = if (genreResult is Result.Success) {
                genreResult.data
            } else {
                emptyList()
            }
            when (movieDetailResult) {
                is Result.Success -> {
                    val data = movieDetailResult.data
                    data.genres.addAll(genreList)
                    MovieDetailUiState.Success(data)
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

    private fun loadCredit(movieId: Long): Flow<Result<Int>> {
        return flow {
            val data = getCreditUseCase.invoke(movieId)
            emit(data)
        }
    }
}