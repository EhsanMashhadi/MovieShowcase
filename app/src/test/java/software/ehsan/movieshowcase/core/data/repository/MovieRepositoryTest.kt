package software.ehsan.movieshowcase.core.data.repository

import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import software.ehsan.movieshowcase.core.database.dao.MovieDao
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.core.network.mapper.asDomain
import software.ehsan.movieshowcase.core.network.service.ApiException
import software.ehsan.movieshowcase.core.network.service.api.MovieApiService
import software.ehsan.movieshowcase.fixtures.MovieFixture
import software.ehsan.movieshowcase.fixtures.MoviesResponseFixture
import software.ehsan.movieshowcase.util.TestDispatcherProvider


class MovieRepositoryTest {

    @MockK
    lateinit var movieApiService: MovieApiService

    @MockK
    lateinit var genreRepository: GenreRepository

    @MockK
    lateinit var movieDao: MovieDao

    private lateinit var moviesRepository: MovieRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        val dispatcherProvider = TestDispatcherProvider()
        moviesRepository = MovieRepositoryImpl(
            movieApiService = movieApiService,
            genreRepository = genreRepository,
            movieDao = movieDao,
            dispatcherProvider = dispatcherProvider
        )
    }

    @Test
    fun getTopMovies_apiReturnEmpty_emptyTopMovies() = runTest {
        coEvery { movieApiService.getTopMovies(any()) } returns Response.success(MovieFixture.emptyMovieResponse)
        coEvery { genreRepository.getGenresMapping() } returns emptyMap()
        val response = moviesRepository.getTopMovies(1)
        assert(response.isSuccess)
        assertEquals(1, response.getOrThrow().page)
        assertEquals(1, response.getOrThrow().totalPages)
        assertEquals(0, response.getOrThrow().totalResultsCount)
        assertEquals(emptyList<Movie>(), response.getOrThrow().results)
    }

    @Test
    fun getTopMovies_apiReturnFiveItems_topFiveItems() = runTest {
        coEvery { movieApiService.getTopMovies(any()) } returns Response.success(MovieFixture.fiveMoviesResponse)
        coEvery { genreRepository.getGenresMapping() } returns emptyMap()
        val response = moviesRepository.getTopMovies(1)
        assert(response.isSuccess)
        assertEquals(1, response.getOrThrow().page)
        assertEquals(1, response.getOrThrow().totalPages)
        assertEquals(5, response.getOrThrow().totalResultsCount)
        assertEquals(
            MovieFixture.fiveMoviesResponse.asDomain(genreRepository).results,
            response.getOrThrow().results
        )
    }

    @Test
    fun getTopMovies_apiReturnError_exceptionOccurred() = runTest {
        coEvery { movieApiService.getTopMovies(any()) } returns errorResponse(400)
        val response = moviesRepository.getTopMovies(1)
        assert(response.isFailure)
        assertEquals(null, response.getOrNull())
        assertEquals(
            ApiException.BadRequestException(error = ERROR_BODY_CONTENT), response.exceptionOrNull()
        )
    }

    @Test
    fun getTopMovies_apiReturnEmptyContent_returnFailResult() = runTest {
        coEvery { movieApiService.getTopMovies(any()) } returns Response.success(null)
        coEvery { genreRepository.getGenresMapping() } returns emptyMap()
        val response = moviesRepository.getTopMovies(1)
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
        coEvery { movieApiService.getTopMovies(any()) } throws runtimeException
        val response = moviesRepository.getTopMovies(1)
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
        coEvery { genreRepository.getGenresMapping() } returns emptyMap()
        val response = moviesRepository.getLatestMovies(genre = null, releaseDateLte = null)
        assert(response.isSuccess)
        assertEquals(
            MovieFixture.fiveMoviesResponse.results.first().asDomain(null),
            response.getOrThrow().results[0]
        )
    }

    @Test
    fun getAllBookmarkedMovies_throwException_returnFailureResult() = runTest {
        coEvery { movieDao.getAllMovies() } returns flow { throw Exception("") }
        moviesRepository.getAllBookmarkedMovies().test {
            val response = awaitItem()
            assert(response.isFailure)
            awaitComplete()
        }
    }

    @Test
    fun getAllBookmarkedMovies_returnMovies_returnSuccessResult() = runTest {
        coEvery { movieDao.getAllMovies() } returns flow { emit(MovieFixture.moviesEntity(5)) }
        moviesRepository.getAllBookmarkedMovies().test {
            val response = awaitItem()
            assert(response.isSuccess)
            assertEquals(5, response.getOrThrow().size)
            awaitComplete()
        }
    }

    @Test
    fun saveMovie_throwException_returnFailureResult() = runTest {
        val movie = MovieFixture.movie(1).first()
        coEvery { movieDao.insertMovie(any()) } throws Exception("Database Error")
        val response = moviesRepository.insertMovie(movie)
        assert(response.isFailure)
        assertEquals("Database Error", response.exceptionOrNull()?.message)
    }

    @Test
    fun saveMovie_itemIsNotSaved_returnFailureResult() = runTest {
        val movie = MovieFixture.movie(1).first()
        coEvery { movieDao.insertMovie(any()) } returns -1
        val response = moviesRepository.insertMovie(movie)
        assert(response.isFailure)
    }

    @Test
    fun saveMovie_itemSaved_returnSuccessResult() = runTest {
        val movie = MovieFixture.movie(1).first()
        coEvery { movieDao.insertMovie(any()) } returns 1
        val response = moviesRepository.insertMovie(movie)
        assert(response.isSuccess)
    }

    @Test
    fun deleteMovie_throwException_returnFailureResult() = runTest {
        val movie = MovieFixture.movie(1).first()
        coEvery { movieDao.delete(any()) } throws Exception("Database Error")
        val response = moviesRepository.deleteMovie(movie)
        assert(response.isFailure)
        assertEquals("Database Error", response.exceptionOrNull()?.message)
    }

    @Test
    fun deleteMovie_itemDeleted_returnFailureResult() = runTest {
        val movie = MovieFixture.movie(1).first()
        coEvery { movieDao.delete(any()) } returns -1
        val response = moviesRepository.deleteMovie(movie)
        assert(response.isFailure)
    }

    @Test
    fun deleteMovie_itemDeleted_returnSuccessResult() = runTest {
        val movie = MovieFixture.movie(1).first()
        coEvery { movieDao.delete(any()) } returns 1
        val response = moviesRepository.deleteMovie(movie)
        assert(response.isSuccess)
    }

    @Test
    fun searchMovie_returnMovies_returnSuccessResult() = runTest {
        val query = "test"
        val returnMovies = MoviesResponseFixture.fiveMoviesResponse
        coEvery {
            movieApiService.search(
                any(),
                page = any()
            )
        } returns Response.success(returnMovies)
        coEvery { genreRepository.getGenresMapping() } returns emptyMap()
        val response = moviesRepository.search(query)
        val movies = response.asSnapshot()
        assertEquals(
            returnMovies.asDomain(genreRepository).results,
            movies
        )
    }

    @Test
    fun searchMovie_returnEmptyMovies_returnSuccessResult() = runTest {
        val query = "test"
        coEvery {
            movieApiService.search(
                any(),
                any()
            )
        } returns Response.success(MovieFixture.emptyMovieResponse)
        coEvery { genreRepository.getGenresMapping() } returns emptyMap()
        val movies = moviesRepository.search(query).asSnapshot()
        assertEquals(0, movies.size)
    }

    @Test
    fun searchMovie_returnError_returnFailureResult() = runTest {
        val query = "test"
        coEvery {
            movieApiService.search(
                any(),
                any()
            )
        } returns errorResponse(400)
        val response = moviesRepository.search(query)
        try {
            response.asSnapshot()
        } catch (e: Exception) {
            assertEquals(ERROR_BODY_CONTENT, e.message)
        }
    }
}

const val ERROR_BODY_CONTENT = "Error body content"
fun <T> errorResponse(code: Int): Response<T> {
    val errorResponseBody = ERROR_BODY_CONTENT.toResponseBody(null)
    val response: Response<T> = Response.error(code, errorResponseBody)
    return response
}