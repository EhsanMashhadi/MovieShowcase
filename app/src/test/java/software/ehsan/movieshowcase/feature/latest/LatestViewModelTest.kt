package software.ehsan.movieshowcase.feature.latest

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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import software.ehsan.movieshowcase.core.model.Movies
import software.ehsan.movieshowcase.domain.GetGenresUseCase
import software.ehsan.movieshowcase.domain.GetLatestMoviesUseCase
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


    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun initiateViewModel_showGenreAndMoviesLoadedState() = runTest {
        val detailViewModel = LatestViewModel(
            getLatestMoviesUseCase = getLatestMoviesUseCase,
            getGenresUseCase = getGenreUseCase,
            context = RuntimeEnvironment.getApplication()
        )
        val movie = MovieFixture.movies(1)
        coEvery { getLatestMoviesUseCase.invoke(any()) } returns flowOf(Result.success(movie))
        coEvery { getGenreUseCase.invoke() } returns Result.success(emptyList())
        Assert.assertNull(detailViewModel.uiState.value.selectedGenre)
        Assert.assertTrue(detailViewModel.uiState.value.genresState is LatestViewModel.UiState.Loading)
        Assert.assertTrue(detailViewModel.uiState.value.moviesState is LatestViewModel.UiState.Loading)
    }

    @Test
    fun getGenres_returnGenres_showGenreSuccessState() = runTest {
        val genres = GenreFixture.genres(5)
        val detailViewModel = LatestViewModel(
            getLatestMoviesUseCase = getLatestMoviesUseCase,
            getGenresUseCase = getGenreUseCase,
            context = RuntimeEnvironment.getApplication()
        )
        coEvery { getGenreUseCase.invoke() } returns Result.success(genres)
        coEvery { getLatestMoviesUseCase.invoke(any()) } returns flowOf(
            Result.success(
                Movies(
                    page = 1,
                    totalPages = 1,
                    totalResults = 1,
                    results = emptyList()
                )
            )
        )
        detailViewModel.handleIntent(LatestViewModel.LatestIntent.LoadGenres)
        advanceUntilIdle()
        Assert.assertTrue(detailViewModel.uiState.value.genresState is LatestViewModel.UiState.Success)
        Assert.assertEquals(
            genres.size + 1, // +1 for "All Genres"
            (detailViewModel.uiState.value.genresState as LatestViewModel.UiState.Success).data.size
        )
    }

    @Test
    fun getLatestMovies_returnMovies_showMoviesSuccessState() = runTest {
        val movies = MovieFixture.movies(5)
        val detailViewModel = LatestViewModel(
            getLatestMoviesUseCase = getLatestMoviesUseCase,
            getGenresUseCase = getGenreUseCase,
            context = RuntimeEnvironment.getApplication()
        )
        coEvery { getGenreUseCase.invoke() } returns Result.success(emptyList())
        coEvery { getLatestMoviesUseCase.invoke(any()) } returns flowOf(Result.success(movies))
        detailViewModel.handleIntent(LatestViewModel.LatestIntent.LoadGenres)
        advanceUntilIdle()
        Assert.assertTrue(detailViewModel.uiState.value.moviesState is LatestViewModel.UiState.Success)
        Assert.assertEquals(
            movies.results.size,
            (detailViewModel.uiState.value.moviesState as LatestViewModel.UiState.Success).data.results.size
        )
    }

    @Test
    fun getGenres_returnFailure_showNoGenres() = runTest {
        val exception = Exception("Error fetching genres")
        val detailViewModel = LatestViewModel(
            getLatestMoviesUseCase = getLatestMoviesUseCase,
            getGenresUseCase = getGenreUseCase,
            context = RuntimeEnvironment.getApplication()
        )
        coEvery { getGenreUseCase.invoke() } returns Result.failure(exception)
        coEvery { getLatestMoviesUseCase.invoke(any()) } returns flowOf(
            Result.success(
                Movies(
                    page = 1,
                    totalPages = 1,
                    totalResults = 1,
                    results = emptyList()
                )
            )
        )
        detailViewModel.handleIntent(LatestViewModel.LatestIntent.LoadGenres)
        advanceUntilIdle()
        Assert.assertTrue(detailViewModel.uiState.value.genresState is LatestViewModel.UiState.Success)
        Assert.assertEquals(
            0,
            (detailViewModel.uiState.value.genresState as LatestViewModel.UiState.Success).data.size
        )
    }

    @Test
    fun getLatestMovies_returnFailure_showMoviesErrorState() = runTest {
        val exception = Exception("Error fetching movies")
        val detailViewModel = LatestViewModel(
            getLatestMoviesUseCase = getLatestMoviesUseCase,
            getGenresUseCase = getGenreUseCase,
            context = RuntimeEnvironment.getApplication()
        )
        coEvery { getGenreUseCase.invoke() } returns Result.success(emptyList())
        coEvery { getLatestMoviesUseCase.invoke(any()) } returns flowOf(Result.failure(exception))
        detailViewModel.handleIntent(LatestViewModel.LatestIntent.LoadLatest(selectedGenre = null))
        advanceUntilIdle()
        Assert.assertTrue(detailViewModel.uiState.value.moviesState is LatestViewModel.UiState.Error)
        Assert.assertEquals(
            exception.message,
            (detailViewModel.uiState.value.moviesState as LatestViewModel.UiState.Error).message
        )
    }
}