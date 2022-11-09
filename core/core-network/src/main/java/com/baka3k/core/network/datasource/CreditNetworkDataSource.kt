package com.baka3k.core.network.datasource

import com.baka3k.core.common.result.Result
import com.baka3k.core.model.PagingInfo
import com.baka3k.core.network.model.NetworkCreditsResponse

interface CreditNetworkDataSource {
    suspend fun getCredits(movieId: Int): Result<NetworkCreditsResponse>
}