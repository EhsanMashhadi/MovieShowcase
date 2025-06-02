package software.ehsan.movieshowcase.core.data.repository

import kotlinx.coroutines.flow.Flow
import software.ehsan.movieshowcase.core.model.Genre
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.core.model.Movies

interface MovieRepository {
    suspend fun getTopMovies(): Result<Movies>
    suspend fun getLatestMovies(genre: Genre?, releaseDateLte: String?): Result<Movies>
    suspend fun getMovieDetails(movieId: Int): Result<Movie>
    suspend fun saveMovie(movie: Movie): Result<Unit>
    suspend fun deleteMovie(movie: Movie): Result<Unit>
    fun getAllBookmarkedMovies(): Flow<Result<List<Movie>>>
}