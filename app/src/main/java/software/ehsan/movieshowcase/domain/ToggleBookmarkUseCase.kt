package software.ehsan.movieshowcase.domain

import software.ehsan.movieshowcase.core.data.repository.MovieRepository
import software.ehsan.movieshowcase.core.model.Movie
import javax.inject.Inject

class ToggleBookmarkUseCase @Inject constructor(private val movieRepository: MovieRepository) {
    suspend operator fun invoke(movie: Movie): Result<Unit> {
        return if (movie.isBookmarked) {
            movieRepository.deleteMovie(movie)
        } else {
            movieRepository.insertMovie(movie)
        }
    }
}