package software.ehsan.movieshowcase.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import software.ehsan.movieshowcase.core.database.dao.MovieDao
import software.ehsan.movieshowcase.core.database.model.MovieEntity

@Database(entities = [MovieEntity::class], version = 1)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}