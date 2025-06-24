package software.ehsan.movieshowcase.feature.search

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.testing.asPagingSourceFactory
import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import software.ehsan.movieshowcase.core.model.Movie
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
        val pagingSourceFactory =
            expectedMovies.results.asPagingSourceFactory()
        val testScope = this
        val pagedMoviesFromPagerFlow: Flow<PagingData<Movie>> = Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.cachedIn(testScope)

        val expectedPagedMovies = PagedMovies(
            movies = pagedMoviesFromPagerFlow,
            totalResult = expectedMovies.totalResults
        )
        coEvery { searchMovieUseCase.invoke(any()) } returns flowOf(expectedPagedMovies)
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
            assertEquals(expectedMovies.totalResults, successState.totalResult)
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