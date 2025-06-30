package software.ehsan.movieshowcase.domain

import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import software.ehsan.movieshowcase.core.data.repository.MovieRepository
import software.ehsan.movieshowcase.fixtures.MovieFixture

class GetSingleLatestMovieUseCaseTest {

    @MockK
    private lateinit var movieRepository: MovieRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getLatestMovieUseCase_returnLatestMovie_expectSuccess() = runTest {
        val expectedMovies = MovieFixture.movie(1).first()
        coEvery { movieRepository.getSingleLatestMovie() } returns Result.success(expectedMovies)
        val getLatestMoviesUseCase = GetSingleLatestMovieUseCase(movieRepository)
        val result = getLatestMoviesUseCase.invoke()
        result.test {
            val returnedMovies = awaitItem()
            assert(returnedMovies.isSuccess)
            Assert.assertEquals(expectedMovies, returnedMovies.getOrNull())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getLatestMovieUseCase_returnFailure_expectFailure() = runTest {
        val expectedException = Exception("expected exception")
        coEvery { movieRepository.getSingleLatestMovie() } returns Result.failure(expectedException)
        val getLatestMoviesUseCase = GetSingleLatestMovieUseCase(movieRepository)
        val result = getLatestMoviesUseCase.invoke()
        result.test {
            val returnedMovies = awaitItem()
            assert(returnedMovies.isFailure)
            try {
                returnedMovies.getOrThrow()
            } catch (exception: Exception) {
                Assert.assertEquals(expectedException, exception)

            }
            cancelAndIgnoreRemainingEvents()
        }
    }
}