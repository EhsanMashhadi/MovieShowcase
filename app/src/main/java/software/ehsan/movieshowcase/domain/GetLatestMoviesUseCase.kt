package software.ehsan.movieshowcase.domain

import software.ehsan.movieshowcase.core.data.repository.MovieRepository
import software.ehsan.movieshowcase.core.model.Genre
import software.ehsan.movieshowcase.core.model.Movies
import software.ehsan.movieshowcase.core.util.getCurrentDateFormatted
import javax.inject.Inject

class GetLatestMoviesUseCase @Inject constructor(private val movieRepository: MovieRepository) {
    suspend operator fun invoke(genre: Genre? = null): Result<Movies> {
        return movieRepository.getLatestMovies(
            genre = genre,
            releaseDateLte = getCurrentDateFormatted()
        )
    }
}