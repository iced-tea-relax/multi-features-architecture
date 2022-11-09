package com.baka3k.test.feature.movie.interactor

import com.baka3k.core.common.interactor.UpStreamSingleUseCase
import com.baka3k.core.common.result.Result
import com.baka3k.core.common.result.asResult
import com.baka3k.core.data.movie.repository.MovieRepository
import com.baka3k.core.model.Movie
import com.baka3k.core.model.PagingInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMoviePopularUseCase @Inject constructor(
) : UpStreamSingleUseCase<Flow<Result<List<Movie>>>> {
    @Inject lateinit var movieRepository: MovieRepository
    private val pageInfo = PagingInfo(page = 0, numberItems = 20)
    override fun invoke(): Flow<Result<List<Movie>>> {
        return movieRepository.getPopularMovieStream().asResult()
    }

    suspend fun loadMore() {
        pageInfo.page += 1
        movieRepository.loadMorePopular(pageInfo)
    }
}