package com.baka3k.core.data.movie.repository.real

import com.baka3k.core.data.Synchronizer
import com.baka3k.core.data.movie.model.asEntity
import com.baka3k.core.data.movie.repository.GenreRepository
import com.baka3k.core.database.dao.GenreDao
import com.baka3k.core.database.dao.MovieGenreDao
import com.baka3k.core.database.model.asExternalModel
import com.baka3k.core.model.Genre
import com.baka3k.core.network.datasource.GenreNetworkDataSource
import com.baka3k.core.network.model.NetworkGenre
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GenreRepositoryImpl @Inject constructor(
    private val genreDao: GenreDao,
    private val movieGenreDao: MovieGenreDao,
    private val genreNetworkDataSource: GenreNetworkDataSource
) : GenreRepository {
    override fun getGenres(movieId: Long): Flow<List<Genre>> =
        movieGenreDao.getGenreEntitiesStream(movieId).map { genreEntities ->
            genreEntities.map { genre ->
                genre.asExternalModel()
            }
        }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        when (val networkGenresResult = genreNetworkDataSource.getGenres()) {
            is com.baka3k.core.common.result.Result.Success -> {
                val genres = networkGenresResult.data
                genreDao.insertOrIgnoreGenre(genres.map(NetworkGenre::asEntity))
            }
        }
        return true
    }
}