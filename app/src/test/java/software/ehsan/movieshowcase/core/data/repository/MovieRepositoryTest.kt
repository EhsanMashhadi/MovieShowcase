package software.ehsan.movieshowcase.core.data.repository

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import software.ehsan.movieshowcase.core.cache.MovieCacheImpl
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.core.network.mapper.asDomain
import software.ehsan.movieshowcase.core.network.service.ApiException
import software.ehsan.movieshowcase.core.network.service.api.GenreApiService
import software.ehsan.movieshowcase.core.network.service.api.MovieApiService
import software.ehsan.movieshowcase.fixtures.GenreFixture
import software.ehsan.movieshowcase.fixtures.MovieFixture
import software.ehsan.movieshowcase.util.TestDispatcherProvider


class MovieRepositoryTest {

    @MockK
    lateinit var movieApiService: MovieApiService

    @MockK
    lateinit var genreApiService: GenreApiService

    @MockK
    lateinit var movieCacheImpl: MovieCacheImpl

    private lateinit var moviesRepository: MovieRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        val dispatcherProvider = TestDispatcherProvider()
        moviesRepository = MovieRepositoryImpl(
            movieApiService = movieApiService,
            genreApiService = genreApiService,
            movieCache = movieCacheImpl,
            dispatcherProvider = dispatcherProvider
        )
    }

    @Test
    fun getTopMovies_apiReturnEmpty_emptyTopMovies() = runTest {
        coEvery { movieApiService.getTopMovies() } returns Response.success(MovieFixture.emptyMovieResponse)
        coEvery { genreApiService.getMoviesGenreIds() } returns Response.success(GenreFixture.genres)
        val response = moviesRepository.getTopMovies()
        assert(response.isSuccess)
        assertEquals(1, response.getOrThrow().page)
        assertEquals(1, response.getOrThrow().totalPages)
        assertEquals(0, response.getOrThrow().totalResults)
        assertEquals(emptyList<Movie>(), response.getOrThrow().results)
    }

    @Test
    fun getTopMovies_apiReturnFiveItems_topFiveItems() = runTest {
        coEvery { movieApiService.getTopMovies() } returns Response.success(MovieFixture.fiveMoviesResponse)
        coEvery { genreApiService.getMoviesGenreIds() } returns Response.success(GenreFixture.genres)
        coEvery { movieCacheImpl.saveMovie(any()) } just runs
        val response = moviesRepository.getTopMovies()
        assert(response.isSuccess)
        assertEquals(1, response.getOrThrow().page)
        assertEquals(1, response.getOrThrow().totalPages)
        assertEquals(5, response.getOrThrow().totalResults)
        assertEquals(
            MovieFixture.fiveMoviesResponse.asDomain(null).results,
            response.getOrThrow().results
        )
    }

    @Test
    fun getTopMovies_apiReturnError_exceptionOccurred() = runTest {
        coEvery { movieApiService.getTopMovies() } returns errorResponse(400)
        val response = moviesRepository.getTopMovies()
        assert(response.isFailure)
        assertEquals(null, response.getOrNull())
        assertEquals(
            ApiException.BadRequestException(error = ERROR_BODY_CONTENT), response.exceptionOrNull()
        )
    }

    @Test
    fun getTopMovies_apiReturnEmptyContent_returnFailResult() = runTest {
        coEvery { movieApiService.getTopMovies() } returns Response.success(null)
        coEvery { genreApiService.getMoviesGenreIds() } returns Response.success(GenreFixture.genres)
        val response = moviesRepository.getTopMovies()
        assert(response.isFailure)
        assertEquals(null, response.getOrNull())
        assertEquals(
            ApiException.EmptyBodyException("Received successful status 200 but response body was null"),
            response.exceptionOrNull()
        )
    }

    @Test
    fun getTopMovies_apiReturnException_returnFailResult() = runTest {
        val runtimeException = RuntimeException("Network Failure")
        coEvery { movieApiService.getTopMovies() } throws runtimeException
        val response = moviesRepository.getTopMovies()
        assert(response.isFailure)
        assertEquals(null, response.getOrNull())
        assertEquals(ApiException.UnknownException("Network Failure"), response.exceptionOrNull())
    }

    @Test
    fun getLatestMovies_apiReturnSuccess_returnSuccessResult() = runTest {
        coEvery {
            movieApiService.getLatestMovies(
                sortBy = any(),
                releaseDateLte = any(),
                genreId = any()
            )
        } returns Response.success(MovieFixture.fiveMoviesResponse)
        coEvery { genreApiService.getMoviesGenreIds() } returns Response.success(GenreFixture.genres)
        coEvery { movieCacheImpl.saveMovie(any()) } just runs
        val response = moviesRepository.getLatestMovies(genre = null, releaseDateLte = null)
        assert(response.isSuccess)
        assertEquals(
            MovieFixture.fiveMoviesResponse.results.first().asDomain(null),
            response.getOrThrow().results[0]
        )
    }
}

const val ERROR_BODY_CONTENT = "Error body content"
fun <T> errorResponse(code: Int): Response<T> {
    val errorResponseBody = ERROR_BODY_CONTENT.toResponseBody(null)
    val response: Response<T> = Response.error(code, errorResponseBody)
    return response
}