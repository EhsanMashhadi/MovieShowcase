package software.ehsan.movieshowcase.core.data.repository

import kotlinx.coroutines.withContext
import software.ehsan.movieshowcase.core.model.Genre
import software.ehsan.movieshowcase.core.network.mapper.asGenre
import software.ehsan.movieshowcase.core.network.service.ApiException
import software.ehsan.movieshowcase.core.network.service.api.GenreApiService
import software.ehsan.movieshowcase.core.util.DispatcherProvider
import javax.inject.Inject

class GenreRepositoryImpl @Inject constructor(
    private val genreApiService: GenreApiService,
    private val dispatcherProvider: DispatcherProvider
) : GenresRepository {
    override suspend fun getAllGenres(): Result<List<Genre>> = withContext(dispatcherProvider.io) {
        try {
            val genreResponse = genreApiService.getMoviesGenreIds()
            if (!genreResponse.isSuccessful) {
                return@withContext Result.failure(
                    ApiException.ServerException(
                        genreResponse.code(),
                        genreResponse.message()
                    )
                )
            }
            val genres = genreResponse.body()?.genres ?: return@withContext Result.failure(
                ApiException.EmptyBodyException("Received successful status ${genreResponse.code()} but response body was null")
            )
            return@withContext Result.success(genres.map { it.asGenre() })

        } catch (exception: Exception) {
            return@withContext Result.failure(ApiException.UnknownException(exception.localizedMessage))
        }
    }
}