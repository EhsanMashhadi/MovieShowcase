package software.ehsan.movieshowcase.core.data.repository

import kotlinx.coroutines.withContext
import okio.IOException
import software.ehsan.movieshowcase.core.cache.MovieCache
import software.ehsan.movieshowcase.core.cache.mapper.asEntity
import software.ehsan.movieshowcase.core.model.Genre
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.core.model.Movies
import software.ehsan.movieshowcase.core.network.mapper.asDomain
import software.ehsan.movieshowcase.core.network.service.ApiException
import software.ehsan.movieshowcase.core.network.service.api.GenreApiService
import software.ehsan.movieshowcase.core.network.service.api.MovieApiService
import software.ehsan.movieshowcase.core.util.DispatcherProvider
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val movieApiService: MovieApiService,
    private val genreApiService: GenreApiService,
    private val movieCache: MovieCache,
    private val dispatcherProvider: DispatcherProvider
) : MovieRepository {

    override suspend fun getTopMovies(): Result<Movies> = withContext(dispatcherProvider.io) {
        try {
            val topMoviesResponse = movieApiService.getTopMovies()
            if (!topMoviesResponse.isSuccessful) {
                val errorBody =
                    topMoviesResponse.errorBody()?.string() ?: ApiException.UNKNOWN_ERROR
                val exception = when (topMoviesResponse.code()) {
                    400 -> ApiException.BadRequestException(errorBody)
                    401 -> ApiException.UnauthorizedException(errorBody)
                    else -> ApiException.ServerException(topMoviesResponse.code(), errorBody)
                }
                return@withContext Result.failure(exception)
            }
            val moviesResponse = topMoviesResponse.body() ?: return@withContext Result.failure(
                ApiException.EmptyBodyException("Received successful status ${topMoviesResponse.code()} but response body was null")
            )
            val genresMapping = getGenreMapping()
            val moviesList = moviesResponse.results.map { topMovie ->
                val genreNames = topMovie.genres?.mapNotNull { genresMapping[it] }
                val movieDomain = topMovie.asDomain(genreNames)
                movieCache.saveMovie(topMovie.asEntity(genreNames))
                movieDomain
            }
            return@withContext Result.success(
                Movies(
                    moviesResponse.page,
                    moviesList,
                    moviesResponse.totalPages,
                    moviesResponse.totalResults
                )
            )
        } catch (ioException: IOException) {
            return@withContext Result.failure(ApiException.InternetException(ioException.localizedMessage))
        } catch (exception: Exception) {
            return@withContext Result.failure(ApiException.UnknownException(exception.localizedMessage))
        }
    }


    override suspend fun getLatestMovies(genre: Genre?, releaseDateLte: String?): Result<Movies> =
        withContext(dispatcherProvider.io) {
            try {
                val latestMovieResponse = movieApiService.getLatestMovies(
                    releaseDateLte = releaseDateLte,
                    genreId = genre?.id
                )
                if (!latestMovieResponse.isSuccessful) {
                    return@withContext Result.failure(
                        ApiException.ServerException(
                            latestMovieResponse.code(),
                            latestMovieResponse.errorBody()?.string() ?: ApiException.UNKNOWN_ERROR
                        )
                    )
                }
                val moviesResponse =
                    latestMovieResponse.body() ?: return@withContext Result.failure(
                        ApiException.EmptyBodyException("Received successful status ${latestMovieResponse.code()} but response body was null")
                    )
                val genresMapping = getGenreMapping()
                val moviesList = moviesResponse.results.map { topMovie ->
                    val genreNames = topMovie.genres?.mapNotNull { genresMapping[it] }
                    val movieDomain = topMovie.asDomain(genreNames)
                    movieCache.saveMovie(topMovie.asEntity(genreNames))
                    movieDomain
                }
                return@withContext Result.success(
                    Movies(
                        moviesResponse.page,
                        moviesList,
                        moviesResponse.totalPages,
                        moviesResponse.totalResults
                    )
                )
            } catch (ioException: IOException) {
                return@withContext Result.failure(ApiException.InternetException(ioException.localizedMessage))
            } catch (exception: Exception) {
                return@withContext Result.failure(ApiException.UnknownException(exception.localizedMessage))
            }
        }

    override suspend fun getMovieDetails(movieId: Int): Result<Movie> =
        withContext(dispatcherProvider.io) {
            val cachedMovie = movieCache.getMovie(movieId)
            if (cachedMovie != null) {
                return@withContext Result.success(cachedMovie)
            }
            try {
                val moviesDetailsResponse = movieApiService.getMovieDetails(movieId = movieId)
                if (!moviesDetailsResponse.isSuccessful) {
                    return@withContext Result.failure(
                        ApiException.ServerException(
                            moviesDetailsResponse.code(),
                            moviesDetailsResponse.errorBody()?.string()
                                ?: ApiException.UNKNOWN_ERROR
                        )
                    )
                }
                val movieDetail = moviesDetailsResponse.body() ?: return@withContext Result.failure(
                    ApiException.EmptyBodyException("Received successful status ${moviesDetailsResponse.code()} but response body was null")
                )
                val genresMapping = getGenreMapping()
                val genresName = movieDetail.genres?.mapNotNull { genresMapping[it] }
                val movie = movieDetail.asDomain(genresName)
                return@withContext Result.success(movie)
            } catch (exception: Exception) {
                return@withContext Result.failure(ApiException.UnknownException(exception.localizedMessage))
            }
        }

    private suspend fun getGenreMapping(): Map<Int, String> {
        val genresResponse = genreApiService.getMoviesGenreIds()
        return if (genresResponse.isSuccessful) {
            genresResponse.body()?.let { genreResponse ->
                genreResponse.genres.associateBy({ it.id }, { it.name })
            } ?: emptyMap()
        } else emptyMap()
    }
}