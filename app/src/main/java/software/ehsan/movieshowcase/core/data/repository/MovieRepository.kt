package software.ehsan.movieshowcase.core.data.repository

import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.core.model.Movies

interface MovieRepository {
    suspend fun getTopMovies(): Result<Movies>
    suspend fun getLastMovie(): Result<Movie>
    suspend fun getMovieDetails(movieId: Int): Result<Movie>
}