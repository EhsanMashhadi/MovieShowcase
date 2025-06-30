package software.ehsan.movieshowcase.feature.latest

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import software.ehsan.movieshowcase.core.model.Genre
import software.ehsan.movieshowcase.core.model.PagedMovies
import software.ehsan.movieshowcase.domain.GetGenresUseCase
import software.ehsan.movieshowcase.domain.GetLatestMoviesUseCase
import software.ehsan.movieshowcase.domain.ToggleBookmarkUseCase
import software.ehsan.movieshowcase.fixtures.GenreFixture
import software.ehsan.movieshowcase.fixtures.MovieFixture
import software.ehsan.movieshowcase.util.MainDispatcherRule

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
@Config(sdk = [34])
class LatestViewModelTest {

    @get:Rule
    val rule = MainDispatcherRule()

    @MockK
    lateinit var getLatestMoviesUseCase: GetLatestMoviesUseCase

    @MockK
    lateinit var getGenreUseCase: GetGenresUseCase

    @MockK
    lateinit var toggleBookmarkUseCase: ToggleBookmarkUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun initiateViewModel_showGenreAndMoviesLoadedState() = runTest {
        val detailViewModel = LatestViewModel(
            getLatestMoviesUseCase = getLatestMoviesUseCase,
            getGenresUseCase = getGenreUseCase,
            toggleBookmarkUseCase = toggleBookmarkUseCase,
            context = RuntimeEnvironment.getApplication()
        )
        val movies = MovieFixture.movie(5)
        coEvery { getLatestMoviesUseCase.invoke(any()) } returns flowOf(
            PagedMovies(
                movies = flowOf(
                    PagingData.from(movies)
                ), totalResultCount = movies.size
            )
        )
        coEvery { getGenreUseCase.invoke() } returns Result.success(emptyList())
        Assert.assertNull(detailViewModel.uiState.value.selectedGenre)
        Assert.assertTrue(detailViewModel.uiState.value.genres is LatestViewModel.UiState.Idle)
        Assert.assertTrue(detailViewModel.uiState.value.movies is LatestViewModel.UiState.Idle)
    }

    @Test
    fun getGenres_returnGenres_showGenreSuccessState() = runTest {
        val genres = GenreFixture.genres(5)
        val detailViewModel = LatestViewModel(
            getLatestMoviesUseCase = getLatestMoviesUseCase,
            getGenresUseCase = getGenreUseCase,
            toggleBookmarkUseCase = toggleBookmarkUseCase,
            context = RuntimeEnvironment.getApplication()
        )
        coEvery { getGenreUseCase.invoke() } returns Result.success(genres)
        coEvery { getLatestMoviesUseCase.invoke(any()) } returns emptyFlow()
        detailViewModel.handleIntent(LatestViewModel.LatestIntent.LoadGenres)
        advanceUntilIdle()
        Assert.assertTrue(detailViewModel.uiState.value.genres is LatestViewModel.UiState.Success)
        val returnedGenres =
            (detailViewModel.uiState.value.genres as LatestViewModel.UiState.Success).data
        Assert.assertEquals(
            genres.size + 1, // +1 for "All Genres"
            returnedGenres.size
        )
        Assert.assertEquals(
            returnedGenres[0], Genre(id = 0, name = "All")
        )
    }

    @Test
    fun getLatestMovies_returnMovies_showMoviesSuccessState() = runTest {
        val movies = MovieFixture.movie(5)
        val detailViewModel = LatestViewModel(
            getLatestMoviesUseCase = getLatestMoviesUseCase,
            getGenresUseCase = getGenreUseCase,
            toggleBookmarkUseCase = toggleBookmarkUseCase,
            context = RuntimeEnvironment.getApplication()
        )
        coEvery { getGenreUseCase.invoke() } returns Result.success(emptyList())
        coEvery { getLatestMoviesUseCase.invoke(any()) } returns flowOf(
            PagedMovies(
                movies = flowOf(
                    PagingData.from(movies)
                ), totalResultCount = movies.size
            )
        )
        detailViewModel.handleIntent(LatestViewModel.LatestIntent.LoadGenres)
        advanceUntilIdle()
        Assert.assertTrue(detailViewModel.uiState.value.movies is LatestViewModel.UiState.Success)
        val returnedMovies =
            (detailViewModel.uiState.value.movies as LatestViewModel.UiState.Success).data.movies.asSnapshot()
        Assert.assertEquals(
            movies,
            returnedMovies
        )
    }

    @Test
    fun getGenres_returnFailure_showNoGenres() = runTest {
        val exception = Exception("Error fetching genres")
        val detailViewModel = LatestViewModel(
            getLatestMoviesUseCase = getLatestMoviesUseCase,
            getGenresUseCase = getGenreUseCase,
            toggleBookmarkUseCase = toggleBookmarkUseCase,
            context = RuntimeEnvironment.getApplication()
        )
        coEvery { getGenreUseCase.invoke() } returns Result.failure(exception)
        coEvery { getLatestMoviesUseCase.invoke(any()) } returns emptyFlow()
        detailViewModel.handleIntent(LatestViewModel.LatestIntent.LoadGenres)
        advanceUntilIdle()
        Assert.assertTrue(detailViewModel.uiState.value.genres is LatestViewModel.UiState.Success)
        Assert.assertEquals(
            0,
            (detailViewModel.uiState.value.genres as LatestViewModel.UiState.Success).data.size
        )
    }

    @Test
    fun getLatestMovies_returnFailure_showMoviesErrorState() = runTest {
        val exception = Exception("Error fetching movies")
        val detailViewModel = LatestViewModel(
            getLatestMoviesUseCase = getLatestMoviesUseCase,
            getGenresUseCase = getGenreUseCase,
            toggleBookmarkUseCase = toggleBookmarkUseCase,
            context = RuntimeEnvironment.getApplication()
        )
        coEvery { getGenreUseCase.invoke() } returns Result.success(emptyList())
        coEvery { getLatestMoviesUseCase.invoke(any()) } returns flow { throw exception }
        detailViewModel.handleIntent(LatestViewModel.LatestIntent.LoadLatest(selectedGenre = null))
        advanceUntilIdle()
        Assert.assertTrue(detailViewModel.uiState.value.movies is LatestViewModel.UiState.Error)
        Assert.assertEquals(
            exception.message,
            (detailViewModel.uiState.value.movies as LatestViewModel.UiState.Error).message
        )
    }
}