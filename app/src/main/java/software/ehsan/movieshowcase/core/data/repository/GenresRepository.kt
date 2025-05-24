package software.ehsan.movieshowcase.core.data.repository

import software.ehsan.movieshowcase.core.model.Genre

interface GenresRepository {
    suspend fun getAllGenres(): Result<List<Genre>>
}