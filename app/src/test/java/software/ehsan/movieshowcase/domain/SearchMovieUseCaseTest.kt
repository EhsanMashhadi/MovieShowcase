package software.ehsan.movieshowcase.domain

import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import software.ehsan.movieshowcase.core.data.repository.MovieRepository
import software.ehsan.movieshowcase.fixtures.MovieFixture


class SearchMovieUseCaseTest {

    @MockK
    lateinit var movieRepository: MovieRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun searchMovieUseCase_emptyQuery_returnsEmptyResult() = runTest {
        val expectedMovie = MovieFixture.movies(0)
        val searchMovieUseCase = SearchMovieUseCase(movieRepository = movieRepository)
        val result = searchMovieUseCase.invoke("")
        result.test {
            val response = awaitItem()
            assert(response.isSuccess)
            Assert.assertEquals(expectedMovie, response.getOrThrow())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun searchMovieUseCase_validQuery_returnsSearchResults() = runTest {
        val expectedMovies = MovieFixture.movies(size = 3, isBookmarked = true)
        coEvery { movieRepository.search(any()) } returns Result.success(expectedMovies)
        coEvery { movieRepository.getAllBookmarkedMovies() } returns flowOf(
            Result.success(
                MovieFixture.movie(3, isBookmarked = true)
            )
        )
        val searchMovieUseCase = SearchMovieUseCase(movieRepository = movieRepository)
        searchMovieUseCase.invoke("test").test {
            val response = awaitItem()
            assert(response.isSuccess)
            Assert.assertEquals(expectedMovies, response.getOrThrow())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun searchMovieUseCase_noResults_returnsEmptyList() = runTest {
        coEvery { movieRepository.search(any()) } returns Result.success(MovieFixture.movies(0))
        coEvery { movieRepository.getAllBookmarkedMovies() } returns flowOf(
            Result.success(
                MovieFixture.movie(0, isBookmarked = false)
            )
        )
        val searchMovieUseCase = SearchMovieUseCase(movieRepository = movieRepository)
        searchMovieUseCase.invoke("test").test {
            val response = awaitItem()
            assert(response.isSuccess)
            Assert.assertEquals(MovieFixture.movies(0), response.getOrThrow())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun searchMovieUseCase_error_returnsFailure() = runTest {
        val errorMessage = "Network Error"
        coEvery { movieRepository.search(any()) } returns Result.failure(Exception(errorMessage))
        coEvery { movieRepository.getAllBookmarkedMovies() } returns flowOf(
            Result.success(
                MovieFixture.movie(0, isBookmarked = false)
            )
        )
        val searchMovieUseCase = SearchMovieUseCase(movieRepository = movieRepository)
        searchMovieUseCase.invoke("test").test {
            val response = awaitItem()
            assert(response.isFailure)
            Assert.assertTrue(response.exceptionOrNull()?.message?.contains(errorMessage) == true)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun searchMovieUseCase_exceptionMovieResult_returnFailure() = runTest {
        val errorMessage = "Unknown Error"
        coEvery { movieRepository.search(any()) } throws Exception(errorMessage)
        coEvery { movieRepository.getAllBookmarkedMovies() } returns flowOf(
            Result.success(
                MovieFixture.movie(3, isBookmarked = false)
            )
        )
        val searchMovieUseCase = SearchMovieUseCase(movieRepository = movieRepository)
        searchMovieUseCase.invoke("test").test {
            val response = awaitItem()
            assert(response.isFailure)
            Assert.assertTrue(response.exceptionOrNull()?.message?.contains(errorMessage) == true)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun searchMovieUseCase_exceptionMovieBookmarkedResult_returnFailure() = runTest {
        val errorMessage = "Unknown Error"
        coEvery { movieRepository.search(any()) } returns Result.success(MovieFixture.movies(0))
        coEvery { movieRepository.getAllBookmarkedMovies() } returns flow {
            throw Exception(
                errorMessage
            )
        }
        val searchMovieUseCase = SearchMovieUseCase(movieRepository = movieRepository)
        searchMovieUseCase.invoke("test").test {
            val response = awaitItem()
            assert(response.isSuccess)
            cancelAndIgnoreRemainingEvents()
        }
    }
}