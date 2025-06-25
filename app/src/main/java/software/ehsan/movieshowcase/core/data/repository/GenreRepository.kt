package software.ehsan.movieshowcase.core.data.repository

import software.ehsan.movieshowcase.core.model.Genre

interface GenreRepository {
    suspend fun getAllGenres(): Result<List<Genre>>
    suspend fun getGenresMapping(): Map<Int, String>
}