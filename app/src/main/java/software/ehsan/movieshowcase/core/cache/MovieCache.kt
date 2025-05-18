package software.ehsan.movieshowcase.core.cache

import software.ehsan.movieshowcase.core.model.Movie

interface MovieCache {
    suspend fun saveMovie(movie: MovieEntity)
    suspend fun getMovie(movieId: Int): Movie?
}