package software.ehsan.movieshowcase.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import software.ehsan.movieshowcase.core.data.repository.MovieRepository
import software.ehsan.movieshowcase.core.model.Movie
import javax.inject.Inject

class GetBookmarkedMoviesUseCase @Inject constructor(private val movieRepository: MovieRepository) {
    operator fun invoke(): Flow<Result<List<Movie>>> {
        return movieRepository.getAllBookmarkedMovies()
            .map { result ->
                result.map { bookmarkedMoviesList ->
                    bookmarkedMoviesList.map { movie ->
                        movie.copy(isBookmarked = true)
                    }
                }
            }
    }
}