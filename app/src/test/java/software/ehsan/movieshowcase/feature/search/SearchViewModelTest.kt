package software.ehsan.movieshowcase.feature.search

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
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
        Assert.assertTrue(searchViewModel.uiState.value is SearchUiState.Idle)
    }

    @Test
    fun searchMovies_returnMovies_showSuccessSearchResults() = runTest {
        val searchResults = MovieFixture.movies(3)
        coEvery { searchMovieUseCase.invoke(any()) } returns flow {
            delay(100L)
            emit(Result.success(searchResults))
        }

        val searchViewModel = SearchViewModel(
            searchMovieUseCase,
            toggleBookmarkUseCase
        )

        searchViewModel.handleIntent(SearchIntent.UpdateSearchQuery("test"))
        Assert.assertTrue(searchViewModel.uiState.value is SearchUiState.Idle)
        searchViewModel.handleIntent(SearchIntent.Search)
        advanceTimeBy(1L)
        Assert.assertTrue(searchViewModel.uiState.value is SearchUiState.Loading)
        advanceUntilIdle()
        Assert.assertTrue(searchViewModel.uiState.value is SearchUiState.Success)
        val successState = searchViewModel.uiState.value as SearchUiState.Success
        Assert.assertEquals(searchResults, successState.movies)
    }

    @Test
    fun searchMovies_returnNoResults_showSuccessWithNoResults() = runTest {
        coEvery { searchMovieUseCase.invoke(any()) } returns flow {
            delay(100L)
            emit(Result.success(MovieFixture.movies(0)))
        }

        val searchViewModel = SearchViewModel(
            searchMovieUseCase,
            toggleBookmarkUseCase
        )

        searchViewModel.handleIntent(SearchIntent.UpdateSearchQuery("test"))
        Assert.assertTrue(searchViewModel.uiState.value is SearchUiState.Idle)
        searchViewModel.handleIntent(SearchIntent.Search)
        advanceTimeBy(1L)
        Assert.assertTrue(searchViewModel.uiState.value is SearchUiState.Loading)
        advanceUntilIdle()
        Assert.assertTrue(searchViewModel.uiState.value is SearchUiState.Success)
        val successState = searchViewModel.uiState.value as SearchUiState.Success
        Assert.assertEquals(0, successState.movies.results.size)
    }

    @Test
    fun searchMovies_returnsError_showError() = runTest {
        coEvery { searchMovieUseCase.invoke(any()) } returns flow {
            delay(100L)
            emit(Result.failure(Exception("error")))
        }

        val searchViewModel = SearchViewModel(
            searchMovieUseCase,
            toggleBookmarkUseCase
        )

        searchViewModel.handleIntent(SearchIntent.UpdateSearchQuery("test"))
        Assert.assertTrue(searchViewModel.uiState.value is SearchUiState.Idle)
        searchViewModel.handleIntent(SearchIntent.Search)
        advanceTimeBy(1L)
        Assert.assertTrue(searchViewModel.uiState.value is SearchUiState.Loading)
        advanceUntilIdle()
        Assert.assertTrue(searchViewModel.uiState.value is SearchUiState.Error)
    }
}