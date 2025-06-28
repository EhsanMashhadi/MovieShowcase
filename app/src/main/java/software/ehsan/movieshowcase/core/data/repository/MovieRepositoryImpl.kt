package software.ehsan.movieshowcase.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
import software.ehsan.movieshowcase.core.network.service.api.MovieApiService
import software.ehsan.movieshowcase.core.util.DispatcherProvider
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val movieApiService: MovieApiService,
    private val genreRepository: GenreRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val movieDao: MovieDao
) : MovieRepository {

    private val _totalMoviesResultCount = MutableStateFlow<Int>(0)
    override val totalMoviesResultCount: StateFlow<Int> = _totalMoviesResultCount.asStateFlow()

    override val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override suspend fun getTopMovies(page: Int): Result<Movies> =
        withContext(dispatcherProvider.io) {
            try {
                val topMoviesResponse = movieApiService.getTopMovies(page = page)
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
                val moviesList = moviesResponse.asDomain(genreRepository = genreRepository)
                return@withContext Result.success(
                    Movies(
                        moviesResponse.page,
                        moviesList.results,
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
                val moviesList = moviesResponse.asDomain(genreRepository = genreRepository).results
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
                val genresMapping = genreRepository.getGenresMapping()
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

    override fun search(query: String): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20
            ),
            pagingSourceFactory = {
                val pagingSource =
                    MoviesPagingSource(
                        movieApiService,
                        genreRepository,
                        query,
                        _totalMoviesResultCount
                    )
                pagingSource
            }
        ).flow.cachedIn(repositoryScope)
    }
}
