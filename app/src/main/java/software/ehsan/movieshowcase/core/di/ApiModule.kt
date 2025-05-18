package software.ehsan.movieshowcase.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import software.ehsan.movieshowcase.core.network.service.api.GenreApiService
import software.ehsan.movieshowcase.core.network.service.api.MovieApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideMovieApiService(retrofit: Retrofit): MovieApiService {
        return retrofit.create(MovieApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGenreApiService(retrofit: Retrofit): GenreApiService {
        return retrofit.create(GenreApiService::class.java)
    }
}
