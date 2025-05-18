package software.ehsan.movieshowcase.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import software.ehsan.movieshowcase.BuildConfig
import software.ehsan.movieshowcase.core.network.service.interceptors.AuthInterceptor
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, @Named("base_url") baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        networkLoggingInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(networkLoggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("base_url")
    fun provideBaseUrl(): String {
        return "https://api.themoviedb.org/3/"
    }

    @Provides
    @Singleton
    @Named("authorization_token")
    fun provideAuthorizationToken(): String {
        return BuildConfig.AUTHORIZATION_TOKEN
    }

    @Provides
    @Singleton
    fun provideNetworkLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }
}
