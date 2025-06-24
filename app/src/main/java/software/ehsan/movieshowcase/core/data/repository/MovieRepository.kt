package software.ehsan.movieshowcase.core.data.repository

import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import software.ehsan.movieshowcase.core.model.Genre
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.core.model.Movies

interface MovieRepository {
    val totalMoviesResultCount: StateFlow<Int>
    val repositoryScope: CoroutineScope
    suspend fun getTopMovies(): Result<Movies>
    suspend fun getLatestMovies(genre: Genre?, releaseDateLte: String?): Result<Movies>
    suspend fun getMovieDetails(movieId: Int): Result<Movie>
    suspend fun insertMovie(movie: Movie): Result<Unit>
    suspend fun deleteMovie(movie: Movie): Result<Unit>
    fun getAllBookmarkedMovies(): Flow<Result<List<Movie>>>
    fun search(query: String): Flow<PagingData<Movie>>
}