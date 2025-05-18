package software.ehsan.movieshowcase.domain

import software.ehsan.movieshowcase.core.data.repository.MovieRepository
import javax.inject.Inject

class GetTopMoviesUseCase @Inject constructor (private val movieRepository: MovieRepository){
    suspend operator fun invoke() = movieRepository.getTopMovies()
}