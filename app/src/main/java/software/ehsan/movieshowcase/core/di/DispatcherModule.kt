package software.ehsan.movieshowcase.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import software.ehsan.movieshowcase.core.util.DefaultDispatcherProvider
import software.ehsan.movieshowcase.core.util.DispatcherProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    @Singleton
    @Provides
    fun provideDispatcherProvider(): DispatcherProvider {
        return DefaultDispatcherProvider()
    }
}