package software.ehsan.movieshowcase.domain

import software.ehsan.movieshowcase.core.data.repository.GenresRepository
import software.ehsan.movieshowcase.core.model.Genre
import javax.inject.Inject

class GetGenresUseCase @Inject constructor(private val genresRepository: GenresRepository) {
    suspend operator fun invoke(): Result<List<Genre>> {
        return genresRepository.getAllGenres()
    }
}
