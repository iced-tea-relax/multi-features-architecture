package com.baka3k.core.data.movie.repository

import com.baka3k.core.common.result.Result
import com.baka3k.core.data.Syncable
import com.baka3k.core.model.Movie
import com.baka3k.core.model.PagingInfo
import kotlinx.coroutines.flow.Flow

interface MovieRepository{
    fun getPopularMovieStream(): Flow<List<Movie>>
    fun getNowPlayingMovieStream(): Flow<List<Movie>>
    fun getTopRateMovieStream(): Flow<List<Movie>>
    fun getUpCommingMovieStream(): Flow<List<Movie>>
    fun getMovieStream(idMovie: String): Flow<Movie>

    suspend fun loadMorePopular(pagingConfig: PagingInfo): Result<List<Movie>>
    suspend fun loadMoreNowPlaying(pagingConfig: PagingInfo): Result<List<Movie>>

}