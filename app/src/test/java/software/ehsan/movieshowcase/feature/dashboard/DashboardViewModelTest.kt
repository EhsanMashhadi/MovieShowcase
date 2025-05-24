package software.ehsan.movieshowcase.feature.dashboard

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import software.ehsan.movieshowcase.domain.GetLatestMoviesUseCase
import software.ehsan.movieshowcase.domain.GetTopMoviesUseCase
import software.ehsan.movieshowcase.fixtures.MovieFixture
import software.ehsan.movieshowcase.util.MainDispatcherRule

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    @get:Rule
    val rule = MainDispatcherRule()

    @MockK
    lateinit var getTopMoviesUseCase: GetTopMoviesUseCase

    @MockK
    lateinit var getLatestMoviesUseCase: GetLatestMoviesUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun initiateViewModel_initiateViewModel_showIdleState() = runTest {
        val dashboardViewModel = DashboardViewModel(getTopMoviesUseCase, getLatestMoviesUseCase)
        Assert.assertTrue(dashboardViewModel.uiState.value is DashboardState.Idle)
    }

    @Test
    fun loadAllMovies_returnLatestAndTopMovies_showSuccessLatestAndTopMovies() = runTest {
        val latestMovie = MovieFixture.movies(1)
        val topMovies = MovieFixture.movies(5)
        coEvery { getLatestMoviesUseCase.invoke() } returns Result.success(latestMovie)
        coEvery { getTopMoviesUseCase.invoke() } returns Result.success(topMovies)
        val dashboardViewModel = DashboardViewModel(getTopMoviesUseCase, getLatestMoviesUseCase)
        Assert.assertTrue(dashboardViewModel.uiState.value is DashboardState.Idle)
        dashboardViewModel.handleIntent(DashboardIntent.LoadAllMovies)
        Assert.assertTrue(dashboardViewModel.uiState.value is DashboardState.Loading)
        advanceUntilIdle()
        Assert.assertTrue(dashboardViewModel.uiState.value is DashboardState.Success)
        val successState = dashboardViewModel.uiState.value as DashboardState.Success
        Assert.assertEquals(latestMovie.results[0].title, successState.latestMovie?.title)
        Assert.assertEquals(topMovies, successState.topMovies)
    }

    @Test
    fun loadAllMovies_returnOnlyLatestMovie_showSuccessOnlyLatestMovie() = runTest {
        val latestMovie = MovieFixture.movies(1)
        coEvery { getLatestMoviesUseCase.invoke() } returns Result.success(latestMovie)
        coEvery { getTopMoviesUseCase.invoke() } returns Result.failure(Exception("error"))
        val dashboardViewModel = DashboardViewModel(getTopMoviesUseCase, getLatestMoviesUseCase)
        Assert.assertTrue(dashboardViewModel.uiState.value is DashboardState.Idle)
        dashboardViewModel.handleIntent(DashboardIntent.LoadAllMovies)
        Assert.assertTrue(dashboardViewModel.uiState.value is DashboardState.Loading)
        advanceUntilIdle()
        Assert.assertTrue(dashboardViewModel.uiState.value is DashboardState.Success)
        val successState = dashboardViewModel.uiState.value as DashboardState.Success
        Assert.assertEquals(latestMovie.results[0].title, successState.latestMovie?.title)
        Assert.assertNull(successState.topMovies)
    }

    @Test
    fun loadAllMovies_returnOnlyTopMovies_showSuccessOnlyTopMovies() = runTest {
        val topMovies = MovieFixture.movies(5)
        coEvery { getTopMoviesUseCase.invoke() } returns Result.success(topMovies)
        coEvery { getLatestMoviesUseCase.invoke() } returns Result.failure(Exception("error"))
        val dashboardViewModel = DashboardViewModel(getTopMoviesUseCase, getLatestMoviesUseCase)
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
        coEvery { getTopMoviesUseCase.invoke() } returns Result.failure(Exception("error"))
        coEvery { getLatestMoviesUseCase.invoke() } returns Result.failure(Exception("error"))
        val dashboardViewModel = DashboardViewModel(getTopMoviesUseCase, getLatestMoviesUseCase)
        Assert.assertTrue(dashboardViewModel.uiState.value is DashboardState.Idle)
        dashboardViewModel.handleIntent(DashboardIntent.LoadAllMovies)
        Assert.assertTrue(dashboardViewModel.uiState.value is DashboardState.Loading)
        advanceUntilIdle()
        Assert.assertTrue(dashboardViewModel.uiState.value is DashboardState.Error)
    }
}