package com.baka3k.core.data.di

import com.baka3k.core.data.movie.repository.CreditRepository
import com.baka3k.core.data.movie.repository.MovieRepository
import com.baka3k.core.data.movie.repository.MovieTypeRepository
import com.baka3k.core.data.movie.repository.real.CreditRepositoryImpl
import com.baka3k.core.data.movie.repository.real.MovieRepositoryImpl
import com.baka3k.core.data.movie.repository.real.MovieTypeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindsMovieRepository(
        movieRepository: MovieRepositoryImpl
    ): MovieRepository

    @Binds
    fun bindsMovieTypeRepository(
        movieTypeRepository: MovieTypeRepositoryImpl
    ): MovieTypeRepository

    @Binds
    fun bindsCreditRepository(
        creditRepository: CreditRepositoryImpl
    ): CreditRepository
}
