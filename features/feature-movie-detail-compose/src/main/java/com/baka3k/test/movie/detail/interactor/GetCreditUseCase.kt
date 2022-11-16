package com.baka3k.test.movie.detail.interactor

import com.baka3k.core.common.interactor.SingleUseCaseWithParameter
import com.baka3k.core.common.result.Result
import com.baka3k.core.data.movie.repository.CreditRepository
import javax.inject.Inject

class GetCreditUseCase @Inject constructor(
) : SingleUseCaseWithParameter<Long, Result<Int>> {
    @Inject
    lateinit var creditRepository: CreditRepository
    override suspend fun invoke(movieId: Long): Result<Int> {
        return creditRepository.getCredit(movieId = movieId)
    }
}