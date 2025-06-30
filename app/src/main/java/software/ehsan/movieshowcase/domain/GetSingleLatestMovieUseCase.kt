package software.ehsan.movieshowcase.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import software.ehsan.movieshowcase.core.data.repository.MovieRepository
import software.ehsan.movieshowcase.core.model.Movie
import javax.inject.Inject

class GetSingleLatestMovieUseCase @Inject constructor(private val movieRepository: MovieRepository) {
    suspend operator fun invoke(): Flow<Result<Movie>> {
        return flowOf(movieRepository.getSingleLatestMovie())
    }
}
