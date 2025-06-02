package software.ehsan.movieshowcase.feature.detail

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
import software.ehsan.movieshowcase.domain.GetMovieDetailUseCase
import software.ehsan.movieshowcase.domain.ToggleBookmarkUseCase
import software.ehsan.movieshowcase.fixtures.MovieFixture
import software.ehsan.movieshowcase.util.MainDispatcherRule

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    @get:Rule
    val rule = MainDispatcherRule()

    @MockK
    lateinit var getMovieDetailUseCase: GetMovieDetailUseCase

    @MockK
    lateinit var toggleBookmarkUseCase: ToggleBookmarkUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun initiateViewModel_initiateViewModel_showIdleState() = runTest {
        val detailViewModel = DetailViewModel(getMovieDetailUseCase, toggleBookmarkUseCase)
        Assert.assertTrue(detailViewModel.uiState.value is DetailState.Idle)
    }

    @Test
    fun getMovieDetail_returnMovieDetails_showSuccessState() = runTest {
        val movie = MovieFixture.movie(1).first()
        coEvery { getMovieDetailUseCase.invoke(any()) } returns flowOf(Result.success(movie))
        val detailViewModel = DetailViewModel(getMovieDetailUseCase, toggleBookmarkUseCase)
        detailViewModel.handleIntent(DetailIntent.LoadDetail(1))
        Assert.assertTrue(detailViewModel.uiState.value is DetailState.Loading)
        advanceUntilIdle()
        val successState = detailViewModel.uiState.value as DetailState.Success
        Assert.assertEquals(movie, successState.movie)
    }

    @Test
    fun getMovieDetail_returnException_showErrorState() = runTest {
        coEvery { getMovieDetailUseCase.invoke(any()) } returns flowOf(Result.failure(Exception("Error")))
        val detailViewModel = DetailViewModel(getMovieDetailUseCase, toggleBookmarkUseCase)
        detailViewModel.handleIntent(DetailIntent.LoadDetail(1))
        Assert.assertTrue(detailViewModel.uiState.value is DetailState.Loading)
        advanceUntilIdle()
        Assert.assertTrue(detailViewModel.uiState.value is DetailState.Error)
    }

}