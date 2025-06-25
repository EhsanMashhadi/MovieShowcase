package software.ehsan.movieshowcase.feature.search

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import software.ehsan.movieshowcase.core.model.PagedMovies
import software.ehsan.movieshowcase.domain.SearchMovieUseCase
import software.ehsan.movieshowcase.domain.ToggleBookmarkUseCase
import software.ehsan.movieshowcase.feature.search.SearchViewModel.SearchIntent
import software.ehsan.movieshowcase.feature.search.SearchViewModel.SearchUiState
import software.ehsan.movieshowcase.fixtures.MovieFixture
import software.ehsan.movieshowcase.util.MainDispatcherRule

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val rule = MainDispatcherRule()

    @MockK
    lateinit var toggleBookmarkUseCase: ToggleBookmarkUseCase

    @MockK
    lateinit var searchMovieUseCase: SearchMovieUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun initiateViewModel_initiateViewModel_showIdleState() = runTest {
        val searchViewModel = SearchViewModel(searchMovieUseCase, toggleBookmarkUseCase)
        searchViewModel.uiState.test {
            assertEquals(SearchUiState.Idle, awaitItem())
        }
    }

    @Test
    fun searchMovies_returnMovies_showSuccessSearchResults() = runTest {
        val expectedMovies = MovieFixture.movies(3)
        coEvery { searchMovieUseCase.invoke(any()) } returns flowOf(
            PagedMovies(movies = flowOf(PagingData.from(expectedMovies.results)), totalResultCount = expectedMovies.totalResultsCount)
        )
        val searchViewModel = SearchViewModel(
            searchMovieUseCase,
            toggleBookmarkUseCase
        )
        searchViewModel.uiState.test {
            assertEquals(SearchUiState.Idle, awaitItem())
            searchViewModel.handleIntent(SearchIntent.Search("test"))
            advanceTimeBy(300L) // Pass the debounce time
            assertEquals(SearchUiState.Loading, awaitItem())
            val successState = awaitItem()
            assert(successState is SearchUiState.Success)
            successState as SearchUiState.Success
            assertEquals(expectedMovies.totalResultsCount, successState.totalResult)
            val actualMoviesSnapshot = successState.movies.asSnapshot()
            assertEquals(expectedMovies.results, actualMoviesSnapshot)
            cancelAndIgnoreRemainingEvents()
            advanceUntilIdle()
        }
    }

    @Test
    fun searchMovies_returnsError_showError() = runTest {
        coEvery { searchMovieUseCase.invoke(any()) } returns flow {
            throw Exception("Network Error")
        }
        val searchViewModel = SearchViewModel(
            searchMovieUseCase,
            toggleBookmarkUseCase
        )
        searchViewModel.uiState.test {
            assertEquals(SearchUiState.Idle, awaitItem())
            searchViewModel.handleIntent(SearchIntent.Search("test"))
            advanceTimeBy(300L) // Pass the debounce time
            assertEquals(SearchUiState.Loading, awaitItem())
            val errorState = awaitItem()
            assert(errorState is SearchUiState.Error)
            expectNoEvents()
        }
    }
}