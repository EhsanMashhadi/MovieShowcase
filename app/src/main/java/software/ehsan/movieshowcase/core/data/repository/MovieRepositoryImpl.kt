package software.ehsan.movieshowcase.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okio.IOException
import software.ehsan.movieshowcase.core.database.DatabaseException.GenericDatabaseException
import software.ehsan.movieshowcase.core.database.asEntity
import software.ehsan.movieshowcase.core.database.dao.MovieDao
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
    private val dispatcherProvider: DispatcherProvider,
    private val movieDao: MovieDao
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

    override suspend fun insertMovie(movie: Movie) = withContext(dispatcherProvider.io) {
        try {
            val rowId = movieDao.insertMovie(movie.asEntity())
            if (rowId > 0) {
                return@withContext Result.success(Unit)
            }
            return@withContext Result.failure(GenericDatabaseException("Failed to save movie"))
        } catch (exception: Exception) {
            return@withContext Result.failure(GenericDatabaseException(exception.message))
        }
    }

    override suspend fun deleteMovie(movie: Movie) = withContext(dispatcherProvider.io) {
        try {
            val rowId = movieDao.delete(movie.asEntity())
            if (rowId > 0) {
                return@withContext Result.success(Unit)
            }
            return@withContext Result.failure(GenericDatabaseException("Failed to delete movie"))
        } catch (exception: Exception) {
            return@withContext Result.failure(GenericDatabaseException(exception.message))
        }
    }

    override fun getAllBookmarkedMovies(): Flow<Result<List<Movie>>> {
        return movieDao.getAllMovies()
            .map { moviesEntity ->
                Result.success(moviesEntity.map { movieEntity ->
                    movieEntity.asDomain()
                })
            }
            .catch { emit(Result.failure(it)) }
            .flowOn(dispatcherProvider.io)
    }

    override suspend fun search(query: String): Result<Movies> =
        withContext(dispatcherProvider.io) {
            try {
                val searchResponse = movieApiService.search(query)
                if (!searchResponse.isSuccessful) {
                    val errorBody =
                        searchResponse.errorBody()?.string() ?: ApiException.UNKNOWN_ERROR
                    return@withContext Result.failure(Exception(errorBody))
                }
                val moviesResponse = searchResponse.body() ?: return@withContext Result.failure(
                    ApiException.EmptyBodyException("Received successful status ${searchResponse.code()} but response body was null")
                )
                val genresMapping = getGenreMapping()
                val moviesList = moviesResponse.results.map { topMovie ->
                    val genreNames = topMovie.genres?.mapNotNull { genresMapping[it] }
                    val movieDomain = topMovie.asDomain(genreNames)
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
            } catch (exception: Exception) {
                return@withContext Result.failure(exception)
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