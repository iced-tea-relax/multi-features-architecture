package com.baka3k.test.movie.detail.interactor

import com.baka3k.core.common.interactor.UpStreamSingleUseCaseParameter
import com.baka3k.core.common.result.Result
import com.baka3k.core.common.result.asResult
import com.baka3k.core.data.movie.repository.CreditRepository
import com.baka3k.core.model.Cast
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCastUseCase @Inject constructor(
) : UpStreamSingleUseCaseParameter<Long, Flow<Result<List<Cast>>>> {
    @Inject
    lateinit var creditRepository: CreditRepository
    override fun invoke(idMovie: Long): Flow<Result<List<Cast>>> {
        return creditRepository.getCastStream(idMovie).asResult()
    }
}