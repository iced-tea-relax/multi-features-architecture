package com.baka3k.test.movie.detail.interactor

import com.baka3k.core.common.interactor.UpStreamSingleUseCaseParameter
import com.baka3k.core.common.result.Result
import com.baka3k.core.common.result.asResult
import com.baka3k.core.data.movie.repository.CreditRepository
import com.baka3k.core.model.Cast
import com.baka3k.core.model.Crew
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCrewUseCase @Inject constructor(
) : UpStreamSingleUseCaseParameter<Int, Flow<Result<List<Crew>>>> {
    @Inject
    lateinit var creditRepository: CreditRepository
    override fun invoke(idMovie: Int): Flow<Result<List<Crew>>> {
        return creditRepository.getCrewStream(idMovie).asResult()
    }
}