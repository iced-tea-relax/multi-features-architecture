package com.baka3k.core.data.movie.repository.real

import com.baka3k.core.common.result.Result
import com.baka3k.core.data.movie.model.asCastEntity
import com.baka3k.core.data.movie.model.asCrewEntity
import com.baka3k.core.data.movie.repository.CreditRepository
import com.baka3k.core.database.dao.CastDao
import com.baka3k.core.database.dao.CrewDao
import com.baka3k.core.database.model.CastEntity
import com.baka3k.core.database.model.CrewEntity
import com.baka3k.core.database.model.asExternalModel
import com.baka3k.core.datastore.HiPreferencesDataSource
import com.baka3k.core.model.Cast
import com.baka3k.core.model.Crew
import com.baka3k.core.model.Movie
import com.baka3k.core.network.datasource.CreditNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CreditRepositoryImpl @Inject constructor(
    private val castDao: CastDao,
    private val crewDao: CrewDao,
    private val network: CreditNetworkDataSource,
    private val preference: HiPreferencesDataSource
) : CreditRepository {
    override fun getCastStream(movieId: Int): Flow<List<Cast>> {
        return castDao.getCast(movieId).map { data ->
            data.map(CastEntity::asExternalModel)
        }
    }

    override fun getCrewStream(movieId: Int): Flow<List<Crew>> =
        crewDao.getCrew(movieId).map { crewEntities ->
            crewEntities.map(CrewEntity::asExternalModel)
        }

    override suspend fun getCredit(movieId: Int): Result<Int> =
        when (val response = network.getCredits(movieId)) {
            is Result.Success -> {
                val crew = response.data.crew
                val cast = response.data.cast
                crewDao.upsertCrew(crew.map {
                    it.asCrewEntity(movieId)
                })
                castDao.upsertCast(cast.map {
                    it.asCastEntity(movieId)
                })
                Result.Success(1)
            }
            is Result.Error -> {
                Result.Error(response.exception)
            }
            else -> {
                Result.Success(1)
            }
        }
}