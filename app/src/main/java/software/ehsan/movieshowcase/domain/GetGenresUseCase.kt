package software.ehsan.movieshowcase.domain

import software.ehsan.movieshowcase.core.data.repository.GenreRepository
import software.ehsan.movieshowcase.core.model.Genre
import javax.inject.Inject

class GetGenresUseCase @Inject constructor(private val genreRepository: GenreRepository) {
    suspend operator fun invoke(): Result<List<Genre>> {
        return genreRepository.getAllGenres()
    }
}
