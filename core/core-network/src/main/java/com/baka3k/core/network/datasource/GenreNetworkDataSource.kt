package com.baka3k.core.network.datasource

import com.baka3k.core.common.result.Result
import com.baka3k.core.network.model.NetworkGenre

interface GenreNetworkDataSource {
    suspend fun getGenres(): Result<List<NetworkGenre>>
}