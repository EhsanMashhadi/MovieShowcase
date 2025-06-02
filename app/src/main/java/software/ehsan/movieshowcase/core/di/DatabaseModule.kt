package software.ehsan.movieshowcase.core.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import software.ehsan.movieshowcase.core.database.MovieDatabase
import software.ehsan.movieshowcase.core.database.dao.MovieDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "movie_database"

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): MovieDatabase {
        val database = Room.databaseBuilder(
            context,
            MovieDatabase::class.java, DATABASE_NAME
        ).build()
        return database
    }

    @Provides
    @Singleton
    fun provideMovieDao(movieDatabase: MovieDatabase): MovieDao {
        return movieDatabase.movieDao()
    }
}