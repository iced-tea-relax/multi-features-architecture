package com.baka3k.test.movie.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baka3k.core.common.logger.Logger
import com.baka3k.core.common.result.Result
import com.baka3k.core.model.Cast
import com.baka3k.core.model.Crew
import com.baka3k.test.movie.detail.interactor.GetCastUseCase
import com.baka3k.test.movie.detail.interactor.GetCreditUseCase
import com.baka3k.test.movie.detail.interactor.GetCrewUseCase
import com.baka3k.test.movie.detail.interactor.GetMovieDetailUseCase
import com.baka3k.test.movie.detail.navigation.MovieDetailDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    movieDetailUseCase: GetMovieDetailUseCase,
    crewUseCase: GetCrewUseCase,
    castUseCase: GetCastUseCase,
    private val getCreditUseCase: GetCreditUseCase

) : ViewModel() {
    private val movieId: String = checkNotNull(
        savedStateHandle[MovieDetailDestination.movieIdArg]
    )
    private val castStream: Flow<Result<List<Cast>>> = castUseCase.invoke(movieId.toInt())
    private val crewStream: Flow<Result<List<Crew>>> = crewUseCase.invoke(movieId.toInt())
    private val creditStream: Flow<Result<Int>> = loadCredit(movieId.toInt())
    val creditUiState: StateFlow<CreditUiState> = combine(
        creditStream, castStream, crewStream
    ) { creditResult, castResult, crewResult ->
        val creditUiState = when (creditResult) {
            is Result.Loading -> {
                CreditUiState(crewUiState = CrewUiState.Loading, castUiState = CastUiState.Loading)
            }
            is Result.Error -> {
                Logger.d("test","creditResult err $creditResult")
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
    val movieDetailUiState = movieDetailUseCase.invoke(movieId).map {
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
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MovieDetailUiState.Loading
    )

    private fun loadCredit(movieId: Int): Flow<Result<Int>> {
        return channelFlow {
            withContext(Dispatchers.IO) {
                val data = getCreditUseCase.invoke(movieId)
                send(data)
            }
        }
    }
}