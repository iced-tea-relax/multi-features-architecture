package com.baka3k.core.network.datasource.retrofit.movie

import com.baka3k.core.common.result.Result
import com.baka3k.core.model.PagingInfo
import com.baka3k.core.network.BuildConfig
import com.baka3k.core.network.datasource.CreditNetworkDataSource
import com.baka3k.core.network.datasource.GenreNetworkDataSource
import com.baka3k.core.network.datasource.MovieNetworkDataSource

import com.baka3k.core.network.model.NetworkCreditsResponse
import com.baka3k.core.network.model.NetworkGenre
import com.baka3k.core.network.model.NetworkMovie

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Inject

private const val backend_movie_url = "${BuildConfig.BACKEND_MOVIE_URL}/3/movie/"

class RetrofitMovieNetworkDataSource @Inject constructor(
    networkJson: Json
) : MovieNetworkDataSource, CreditNetworkDataSource,GenreNetworkDataSource {
    private val networkApi = Retrofit.Builder()
        .baseUrl(backend_movie_url)
        .client(
            OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor().apply {
                        setLevel(HttpLoggingInterceptor.Level.BASIC)
                    }
                )
                .build()
        )
        .addConverterFactory(networkJson.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(RetrofitMovieNetworkApi::class.java)

    override suspend fun getPopularMovie(pagingInfo: PagingInfo): Result<List<NetworkMovie>> {
        return com.baka3k.core.common.result.runCatching {
            networkApi.getPopularMovie(page = pagingInfo.page).results
        }
    }

    override suspend fun getTopRateMovie(pagingInfo: PagingInfo): Result<List<NetworkMovie>> {
        return com.baka3k.core.common.result.runCatching {
            networkApi.getTopRateMovie(page = pagingInfo.page).results
        }
    }

    override suspend fun getUpCommingMovie(pagingInfo: PagingInfo): Result<List<NetworkMovie>> {
        return com.baka3k.core.common.result.runCatching {
            networkApi.getUpCommingMovie(page = pagingInfo.page).results
        }
    }

    override suspend fun getNowPlayingMovie(pagingInfo: PagingInfo): Result<List<NetworkMovie>> {
        return com.baka3k.core.common.result.runCatching {
            networkApi.getNowPlayingMovie(page = pagingInfo.page).results
        }
    }

    override suspend fun getCredits(movieId: Int): Result<NetworkCreditsResponse> {
        return com.baka3k.core.common.result.runCatching {
            networkApi.getCredits(movieId = movieId)
        }
    }

    override suspend fun getGenres(): Result<List<NetworkGenre>> {
        return com.baka3k.core.common.result.runCatching {
            networkApi.getGenres()
        }
    }
}