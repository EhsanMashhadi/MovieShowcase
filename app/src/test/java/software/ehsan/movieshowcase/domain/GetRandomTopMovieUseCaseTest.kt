package software.ehsan.movieshowcase.domain

import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import software.ehsan.movieshowcase.core.data.repository.MovieRepository
import software.ehsan.movieshowcase.core.model.Movies
import software.ehsan.movieshowcase.fixtures.MovieFixture


class GetRandomTopMovieUseCaseTest {

    @MockK
    lateinit var movieRepository: MovieRepository

    @MockK
    lateinit var enrichMoviesWithBookmarkStatusUseCase: EnrichMoviesWithBookmarkStatusUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getTop10RandomMoviesUseCase_return10Movies_expectSuccessState() = runTest {
        val expectedMovie = MovieFixture.movies(10)
        coEvery { movieRepository.getTopMovies(any()) } returns Result.success(expectedMovie)
        coEvery { enrichMoviesWithBookmarkStatusUseCase.enrichMovieList(any()) } returns flowOf(
            expectedMovie.results
        )
        val getRandomTopMoviesUseCase = GetRandomTopMovieUseCase(
            movieRepository = movieRepository,
            enrichMoviesWithBookmarkStatusUseCase = enrichMoviesWithBookmarkStatusUseCase
        )
        val topMoviesFlow = getRandomTopMoviesUseCase.invoke(10)

        topMoviesFlow.test {
            val topMoviesResult = awaitItem()
            assert(topMoviesResult.isSuccess)
            val movies = topMoviesResult.getOrThrow()
            Assert.assertEquals(
                Movies(
                    -1,
                    totalPages = 1,
                    totalResultsCount = 10,
                    results = expectedMovie.results
                ), movies
            )
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun getTop10RandomMoviesUseCase_returnException_expectFailureState() = runTest {
        val errorMessage = "Network Error"
        coEvery { movieRepository.getTopMovies(any()) } returns Result.failure(
            Exception(
                errorMessage
            )
        )
        coEvery { enrichMoviesWithBookmarkStatusUseCase.enrichMovieList(any()) } returns flowOf(
            emptyList()
        )
        val getRandomTopMoviesUseCase = GetRandomTopMovieUseCase(
            movieRepository = movieRepository,
            enrichMoviesWithBookmarkStatusUseCase = enrichMoviesWithBookmarkStatusUseCase
        )
        val topMoviesFlow = getRandomTopMoviesUseCase.invoke(10)

        topMoviesFlow.test {
            val topMoviesResult = awaitItem()
            assert(topMoviesResult.isFailure)
            try {
                topMoviesResult.getOrThrow()
            } catch (exception: Exception) {
                Assert.assertEquals(errorMessage, exception.message)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }
}