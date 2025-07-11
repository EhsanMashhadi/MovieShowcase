package software.ehsan.movieshowcase.feature.dashboard

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
import software.ehsan.movieshowcase.core.model.Movies
import software.ehsan.movieshowcase.domain.GetRandomTopMovieUseCase
import software.ehsan.movieshowcase.domain.GetSingleLatestMovieUseCase
import software.ehsan.movieshowcase.domain.ToggleBookmarkUseCase
import software.ehsan.movieshowcase.fixtures.MovieFixture
import software.ehsan.movieshowcase.util.MainDispatcherRule

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    @get:Rule
    val rule = MainDispatcherRule()

    @MockK
    lateinit var getTopMoviesUseCase: GetRandomTopMovieUseCase

    @MockK
    lateinit var getSingleLatestMovieUseCase: GetSingleLatestMovieUseCase

    @MockK
    lateinit var toggleBookmarkUseCase: ToggleBookmarkUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun initiateViewModel_initiateViewModel_showIdleState() = runTest {
        val dashboardViewModel =
            DashboardViewModel(
                getTopMoviesUseCase,
                getSingleLatestMovieUseCase,
                toggleBookmarkUseCase
            )
        Assert.assertTrue(dashboardViewModel.uiState.value is DashboardState.Idle)
    }

    @Test
    fun loadAllMovies_returnLatestAndTopMovies_showSuccessLatestAndTopMovies() = runTest {
        val latestMovie = MovieFixture.movie(1).first()
        val topMovies = MovieFixture.movies(5)
        coEvery { getSingleLatestMovieUseCase.invoke() } returns flowOf(
            Result.success(
                latestMovie
            )
        )
        coEvery { getTopMoviesUseCase.invoke() } returns flowOf(Result.success(topMovies))
        val dashboardViewModel =
            DashboardViewModel(
                getTopMoviesUseCase,
                getSingleLatestMovieUseCase,
                toggleBookmarkUseCase
            )
        Assert.assertTrue(dashboardViewModel.uiState.value is DashboardState.Idle)
        dashboardViewModel.handleIntent(DashboardIntent.LoadAllMovies)
        Assert.assertTrue(dashboardViewModel.uiState.value is DashboardState.Loading)
        advanceUntilIdle()
        Assert.assertTrue(dashboardViewModel.uiState.value is DashboardState.Success)
        val successState = dashboardViewModel.uiState.value as DashboardState.Success
        Assert.assertEquals(latestMovie, successState.latestMovie)
        Assert.assertEquals(topMovies, successState.topMovies)
    }

    @Test
    fun loadAllMovies_returnOnlyLatestMovie_showSuccessOnlyLatestMovie() = runTest {
        val latestMovie = MovieFixture.movie(1).first()
        coEvery { getSingleLatestMovieUseCase.invoke() } returns flowOf(
            Result.success(
                latestMovie
            )
        )
        coEvery { getTopMoviesUseCase.invoke() } returns flowOf(Result.failure(Exception("error")))
        val dashboardViewModel =
            DashboardViewModel(
                getTopMoviesUseCase,
                getSingleLatestMovieUseCase,
                toggleBookmarkUseCase
            )
        Assert.assertTrue(dashboardViewModel.uiState.value is DashboardState.Idle)
        dashboardViewModel.handleIntent(DashboardIntent.LoadAllMovies)
        Assert.assertTrue(dashboardViewModel.uiState.value is DashboardState.Loading)
        advanceUntilIdle()
        Assert.assertTrue(dashboardViewModel.uiState.value is DashboardState.Success)
        val successState = dashboardViewModel.uiState.value as DashboardState.Success
        Assert.assertEquals(latestMovie, successState.latestMovie)
        Assert.assertNull(successState.topMovies)
    }

    @Test
    fun loadAllMovies_returnOnlyTopMovies_showSuccessOnlyTopMovies() = runTest {
        val topMovies = MovieFixture.movies(5)
        coEvery { getTopMoviesUseCase.invoke() } returns flowOf(Result.success(topMovies))
        coEvery { getSingleLatestMovieUseCase.invoke() } returns flowOf(
            Result.failure(
                Exception("error")
            )
        )
        val dashboardViewModel =
            DashboardViewModel(
                getTopMoviesUseCase,
                getSingleLatestMovieUseCase,
                toggleBookmarkUseCase
            )
        Assert.assertTrue(dashboardViewModel.uiState.value is DashboardState.Idle)
        dashboardViewModel.handleIntent(DashboardIntent.LoadAllMovies)
        Assert.assertTrue(dashboardViewModel.uiState.value is DashboardState.Loading)
        advanceUntilIdle()
        Assert.assertTrue(dashboardViewModel.uiState.value is DashboardState.Success)
        val successState = dashboardViewModel.uiState.value as DashboardState.Success
        Assert.assertNull(successState.latestMovie)
        Assert.assertEquals(topMovies, successState.topMovies)
    }

    @Test
    fun loadAllMovies_bothReturnError_showError() = runTest {
        coEvery { getTopMoviesUseCase.invoke() } returns flowOf(Result.failure<Movies>(Exception("error")))
        coEvery { getSingleLatestMovieUseCase.invoke() } returns flowOf(
            Result.failure(
                Exception("error")
            )
        )
        val dashboardViewModel =
            DashboardViewModel(
                getTopMoviesUseCase,
                getSingleLatestMovieUseCase,
                toggleBookmarkUseCase
            )
        Assert.assertTrue(dashboardViewModel.uiState.value is DashboardState.Idle)
        dashboardViewModel.handleIntent(DashboardIntent.LoadAllMovies)
        Assert.assertTrue(dashboardViewModel.uiState.value is DashboardState.Loading)
        advanceUntilIdle()
        Assert.assertTrue(dashboardViewModel.uiState.value is DashboardState.Error)
    }
}