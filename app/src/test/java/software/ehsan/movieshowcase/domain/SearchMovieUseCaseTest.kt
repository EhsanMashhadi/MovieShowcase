package software.ehsan.movieshowcase.domain

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import software.ehsan.movieshowcase.core.data.repository.MovieRepository
import software.ehsan.movieshowcase.fixtures.MovieFixture


class SearchMovieUseCaseTest {

    @MockK
    lateinit var movieRepository: MovieRepository

    @MockK
    lateinit var enrichMoviesWithBookmarkStatusUseCase: EnrichMoviesWithBookmarkStatusUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        val testScope = TestScope(StandardTestDispatcher())
        every { movieRepository.repositoryScope } returns testScope
    }

    @Test
    fun searchMovieUseCase_emptyQuery_returnsEmptyResult() = runTest {
        val expectedMovie = MovieFixture.movies(0)
        val searchMovieUseCase = SearchMovieUseCase(
            movieRepository = movieRepository,
            enrichMoviesWithBookmarkStatusUseCase = enrichMoviesWithBookmarkStatusUseCase
        )
        val result = searchMovieUseCase.invoke("")

        result.test {
            val response = awaitItem()
            Assert.assertEquals(expectedMovie.results, response.movies.asSnapshot())
            Assert.assertEquals(0, response.totalResultCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun searchMovieUseCase_validQuery_returnsSearchResults() = runTest {
        val expectedMovies = MovieFixture.movies(size = 3, isBookmarked = true)
        coEvery { movieRepository.totalMoviesResultCount } returns MutableStateFlow(expectedMovies.totalResultsCount)
        coEvery { movieRepository.search(any()) } returns flow { PagingData.from(emptyList()) }
        coEvery { enrichMoviesWithBookmarkStatusUseCase.perform(any()) } returns flowOf(
            PagingData.from(expectedMovies.results)
        )
        val searchMovieUseCase = SearchMovieUseCase(
            movieRepository = movieRepository,
            enrichMoviesWithBookmarkStatusUseCase = enrichMoviesWithBookmarkStatusUseCase
        )
        searchMovieUseCase.invoke("test").test {
            val response = awaitItem()
            Assert.assertEquals(expectedMovies.results, response.movies.asSnapshot())
            Assert.assertEquals(expectedMovies.totalResultsCount, 3)
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun searchMovieUseCase_error_returnsFailure() = runTest {
        val errorMessage = "Network Error"
        coEvery { movieRepository.search(any()) } returns flow { throw Exception(errorMessage) }
        coEvery { movieRepository.totalMoviesResultCount } returns MutableStateFlow(0)
        coEvery { enrichMoviesWithBookmarkStatusUseCase.perform(any()) } returns flowOf(
            PagingData.from(emptyList())
        )
        val searchMovieUseCase = SearchMovieUseCase(
            movieRepository = movieRepository,
            enrichMoviesWithBookmarkStatusUseCase = enrichMoviesWithBookmarkStatusUseCase
        )
        searchMovieUseCase.invoke("test").test {
            val response = awaitItem()
            try {
                response.movies.asSnapshot()
            } catch (exception: Exception) {
                Assert.assertEquals(errorMessage, exception.message)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }
}