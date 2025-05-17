package software.ehsan.movieshowcase.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import software.ehsan.movieshowcase.core.cache.MovieCache
import software.ehsan.movieshowcase.core.cache.MovieCacheImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CacheModule {

    @Binds
    @Singleton
    abstract fun bindCache(movieCacheImpl: MovieCacheImpl): MovieCache
}