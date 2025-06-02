package software.ehsan.movieshowcase.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import software.ehsan.movieshowcase.core.database.model.MovieEntity

@Dao
interface MovieDao {

    @Insert
    fun insertMovie(movie: MovieEntity): Long

    @Delete
    fun delete(movie: MovieEntity): Int

    @Query("SELECT * FROM movies")
    fun getAllMovies(): Flow<List<MovieEntity>>

}