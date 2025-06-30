package software.ehsan.movieshowcase.domain

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import software.ehsan.movieshowcase.core.data.repository.MovieRepository
import software.ehsan.movieshowcase.fixtures.MovieFixture

class GetLatestMoviesUseCaseTest {

    @MockK
    private lateinit var movieRepository: MovieRepository

    @MockK
    private lateinit var enrichMoviesWithBookmarkStatusUseCase: EnrichMoviesWithBookmarkStatusUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getLatestMovieUseCase_returnLatestMovie_expectSuccess() = runTest {
        val expectedMovies = MovieFixture.movie(3)
        coEvery { movieRepository.getLatestMovies(any(), any()) } returns flowOf(
            PagingData.from(
                emptyList()
            )
        )
        every { enrichMoviesWithBookmarkStatusUseCase.enrichPagingMovies(any()) } returns flowOf(
            PagingData.from(expectedMovies)
        )
        coEvery { movieRepository.totalMoviesResultCount } returns MutableStateFlow(expectedMovies.size)
        val getLatestMoviesUseCase =
            GetLatestMoviesUseCase(movieRepository, enrichMoviesWithBookmarkStatusUseCase)
        val result = getLatestMoviesUseCase.invoke()
        result.test {
            val returnedMovies = awaitItem().movies.asSnapshot()
            Assert.assertEquals(expectedMovies, returnedMovies)
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