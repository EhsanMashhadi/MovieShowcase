package software.ehsan.movieshowcase.feature.bookmark

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import software.ehsan.movieshowcase.domain.GetBookmarkedMoviesUseCase
import software.ehsan.movieshowcase.domain.ToggleBookmarkUseCase
import software.ehsan.movieshowcase.fixtures.MovieFixture
import software.ehsan.movieshowcase.util.MainDispatcherRule

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarkViewModelTest {

    @get:Rule
    val rule = MainDispatcherRule()

    @MockK
    lateinit var toggleBookmarkUseCase: ToggleBookmarkUseCase

    @MockK
    lateinit var getBookmarkedMoviesUseCase: GetBookmarkedMoviesUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun initiateViewModel_initiateViewModel_showLoadingState() = runTest {
        val bookmarkViewModel = BookmarkViewModel(getBookmarkedMoviesUseCase, toggleBookmarkUseCase)
        coEvery { getBookmarkedMoviesUseCase.invoke() } returns flowOf(
            Result.success(
                MovieFixture.movie(10)
            )
        )
        Assert.assertTrue(bookmarkViewModel.uiState.value is BookmarkState.Loading)
    }

    @Test
    fun loadAllBookmarkedMovies_returnMovies_showSuccessBookmarkedMovies() = runTest {
        val bookmarkedMovies = MovieFixture.movie(2)
        coEvery { getBookmarkedMoviesUseCase.invoke() } returns flowOf(
            Result.success(
                bookmarkedMovies
            )
        )
        val bookmarkViewModel = BookmarkViewModel(getBookmarkedMoviesUseCase, toggleBookmarkUseCase)
        Assert.assertTrue(bookmarkViewModel.uiState.value is BookmarkState.Loading)
        bookmarkViewModel.handleIntent(BookmarkIntent.LoadBookmarks)
        Assert.assertTrue(bookmarkViewModel.uiState.value is BookmarkState.Loading)
        advanceUntilIdle()
        Assert.assertTrue(bookmarkViewModel.uiState.value is BookmarkState.Success)
        val successState = bookmarkViewModel.uiState.value as BookmarkState.Success
        Assert.assertEquals(bookmarkedMovies, successState.bookmarkedMovies)
    }

    @Test
    fun loadAllBookmarkedMovies_returnNoMovie_showSuccessWithNoMovies() = runTest {
        val bookmarkedMovies = MovieFixture.movie(0)
        coEvery { getBookmarkedMoviesUseCase.invoke() } returns flowOf(
            Result.success(
                bookmarkedMovies
            )
        )
        val bookmarkViewModel = BookmarkViewModel(getBookmarkedMoviesUseCase, toggleBookmarkUseCase)
        Assert.assertTrue(bookmarkViewModel.uiState.value is BookmarkState.Loading)
        bookmarkViewModel.handleIntent(BookmarkIntent.LoadBookmarks)
        Assert.assertTrue(bookmarkViewModel.uiState.value is BookmarkState.Loading)
        advanceUntilIdle()
        Assert.assertTrue(bookmarkViewModel.uiState.value is BookmarkState.Success)
        val successState = bookmarkViewModel.uiState.value as BookmarkState.Success
        Assert.assertEquals(0, successState.bookmarkedMovies.size)
    }

    @Test
    fun loadAllBookmarkedMovies_returnsError_showError() = runTest {
        coEvery { getBookmarkedMoviesUseCase.invoke() } returns flowOf(Result.failure(Exception("error")))
        val bookmarkViewModel = BookmarkViewModel(getBookmarkedMoviesUseCase, toggleBookmarkUseCase)
        Assert.assertTrue(bookmarkViewModel.uiState.value is BookmarkState.Loading)
        advanceUntilIdle()
        bookmarkViewModel.handleIntent(BookmarkIntent.LoadBookmarks)
        Assert.assertTrue(bookmarkViewModel.uiState.value is BookmarkState.Loading)
        advanceUntilIdle()
        Assert.assertTrue(bookmarkViewModel.uiState.value is BookmarkState.Error)
    }

    @Test
    fun bookmarkMovie_returnsError_showError() = runTest {
        coEvery { toggleBookmarkUseCase.invoke(any()) } returns Result.failure(Exception("error"))
        coEvery { getBookmarkedMoviesUseCase.invoke() } returns flowOf(
            Result.success(
                MovieFixture.movie(
                    1
                )
            )
        )
        val bookmarkViewModel = BookmarkViewModel(getBookmarkedMoviesUseCase, toggleBookmarkUseCase)
        Assert.assertTrue(bookmarkViewModel.uiState.value is BookmarkState.Loading)
        advanceUntilIdle()
        bookmarkViewModel.handleIntent(BookmarkIntent.BookmarkMovie(MovieFixture.movie(1).first()))
        advanceUntilIdle()
        Assert.assertTrue(bookmarkViewModel.uiState.value is BookmarkState.Error)
    }
}