package com.baka3k.core.data.movie.repository.real

import com.baka3k.core.common.result.Result
import com.baka3k.core.data.movie.model.asEntity
import com.baka3k.core.data.movie.model.asMovieTypeNowPlayingEntity
import com.baka3k.core.data.movie.model.asMovieTypePopularEntity
import com.baka3k.core.data.movie.model.asNowPlayingMovie
import com.baka3k.core.data.movie.model.asPopularMovie
import com.baka3k.core.data.movie.repository.MovieRepository
import com.baka3k.core.database.dao.MovieDao
import com.baka3k.core.database.dao.MovieGenreDao
import com.baka3k.core.database.dao.MovieTypeDao
import com.baka3k.core.database.model.GenreEntity
import com.baka3k.core.database.model.MovieEntity
import com.baka3k.core.database.model.MovieGenreCrossRef
import com.baka3k.core.database.model.asExternalModel
import com.baka3k.core.datastore.HiPreferencesDataSource
import com.baka3k.core.model.Genre
import com.baka3k.core.model.Movie
import com.baka3k.core.model.PagingInfo
import com.baka3k.core.network.datasource.MovieNetworkDataSource
import com.baka3k.core.network.model.NetworkMovie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val movieDao: MovieDao,
    private val movieTypeDao: MovieTypeDao,
    private val movieGenreDao: MovieGenreDao,
    private val network: MovieNetworkDataSource,
    private val preference: HiPreferencesDataSource
) : MovieRepository {
    override fun getNowPlayingMovieStream(): Flow<List<Movie>> =
        movieDao.getNowPlayingEntitiesStream().map { data ->
            data.map { it.asExternalModel() }
        }

    override fun getPopularMovieStream(): Flow<List<Movie>> =
        movieDao.getPolularMovieEntitiesStream().map { data ->
            data.map { it.asExternalModel() }
        }

    override fun getTopRateMovieStream(): Flow<List<Movie>> =
        movieDao.getTopRateMovieEntitiesStream().map { data ->
            data.map { it.asExternalModel() }
        }

    override fun getUpCommingMovieStream(): Flow<List<Movie>> =
        movieDao.getUpCommingMovieEntitiesStream().map { data ->
            data.map { it.asExternalModel() }
        }

    override fun getMovieStream(movieId: Long): Flow<Movie> {
        return movieDao.getMovieEntity(movieId).map(MovieEntity::asExternalModel)
    }

    override fun getGenreStream(movieId: Long): Flow<List<Genre>> =
        movieGenreDao.getGenreEntitiesStream(movieId = movieId).map { genres ->
            genres.map(GenreEntity::asExternalModel)
        }

    override suspend fun loadMorePopular(pageinfo: PagingInfo): Result<List<Movie>> {
        return when (val response = network.getPopularMovie(pageinfo)) {
            is Result.Success -> {
                val data = response.data
                movieDao.upsertMovie(data.map(NetworkMovie::asEntity))
                movieTypeDao.insertOrIgnoreMovieType(data.map(NetworkMovie::asMovieTypePopularEntity))

                val mutableList = buildMovieGenreEntities(data)
                movieGenreDao.insertOrIgnoreMovieGenre(mutableList)
                Result.Success(data.map(NetworkMovie::asPopularMovie))
            }
            is Result.Error -> {
                Result.Error(response.exception)
            }
            else -> {
                Result.Success(emptyList())
            }
        }
    }

    private fun buildMovieGenreEntities(data: List<NetworkMovie>): List<MovieGenreCrossRef> {
        val movieGenreCrossRefs = mutableListOf<MovieGenreCrossRef>()
        data.forEach { networkMovie ->
            networkMovie.genreIds.forEach {
                movieGenreCrossRefs.add(MovieGenreCrossRef(networkMovie.id, it))
            }
        }
        return movieGenreCrossRefs
    }

    override suspend fun loadMoreNowPlaying(pageinfo: PagingInfo): Result<List<Movie>> {
        return when (val response = network.getNowPlayingMovie(pageinfo)) {
            is Result.Success -> {
                val data = response.data
                movieDao.upsertMovie(data.map(NetworkMovie::asEntity))
                movieTypeDao.insertOrIgnoreMovieType(data.map(NetworkMovie::asMovieTypeNowPlayingEntity))

                val mutableList = buildMovieGenreEntities(data)
                movieGenreDao.insertOrIgnoreMovieGenre(mutableList)
                Result.Success(data.map(NetworkMovie::asNowPlayingMovie))
            }
            is Result.Error -> {
                Result.Error(response.exception)
            }
            else -> {
                Result.Success(emptyList())
            }
        }
    }

    companion object {
        private const val TAG = "MovieRepositoryImpl"
    }
}