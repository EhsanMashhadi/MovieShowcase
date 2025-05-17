package software.ehsan.movieshowcase.core.cache

import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.core.network.mapper.asDomain
import software.ehsan.movieshowcase.core.network.service.ApiException
import javax.inject.Inject

class MovieCacheImpl @Inject constructor() : MovieCache {
    private val moviesCache = mutableMapOf<Int, MovieEntity>()
    override suspend fun saveMovie(movie: MovieEntity) {
        moviesCache[movie.id] = movie
    }

    override suspend fun getMovie(movieId: Int): Movie? {
        return moviesCache[movieId]?.asDomain()
    }
}