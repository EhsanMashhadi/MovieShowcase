package software.ehsan.movieshowcase.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import software.ehsan.movieshowcase.core.data.repository.GenreRepository
import software.ehsan.movieshowcase.core.data.repository.GenreRepositoryImpl
import software.ehsan.movieshowcase.core.data.repository.MovieRepository
import software.ehsan.movieshowcase.core.data.repository.MovieRepositoryImpl
import software.ehsan.movieshowcase.core.data.repository.UserSettingRepository
import software.ehsan.movieshowcase.core.data.repository.UserSettingRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(movieRepositoryImpl: MovieRepositoryImpl): MovieRepository

    @Binds
    @Singleton
    abstract fun bindGenreRepository(genreRepositoryImpl: GenreRepositoryImpl): GenreRepository

    @Binds
    @Singleton
    abstract fun bindUserSettingRepository(userSettingRepositoryImpl: UserSettingRepositoryImpl): UserSettingRepository
}