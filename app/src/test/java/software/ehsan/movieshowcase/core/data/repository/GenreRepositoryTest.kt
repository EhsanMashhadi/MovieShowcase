package software.ehsan.movieshowcase.core.data.repository

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import software.ehsan.movieshowcase.core.network.mapper.asGenre
import software.ehsan.movieshowcase.core.network.model.GenresResponse
import software.ehsan.movieshowcase.core.network.service.ApiException
import software.ehsan.movieshowcase.core.network.service.api.GenreApiService
import software.ehsan.movieshowcase.fixtures.GenreFixture
import software.ehsan.movieshowcase.util.TestDispatcherProvider


class GenreRepositoryTest {

    @MockK
    lateinit var genreApiService: GenreApiService

    private lateinit var genreRepository: GenreRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        val dispatcherProvider = TestDispatcherProvider()
        genreRepository = GenreRepositoryImpl(
            genreApiService = genreApiService,
            dispatcherProvider = dispatcherProvider
        )
    }

    @Test
    fun getGenres_apiReturnEmpty_emptyGenres() = runTest {
        coEvery { genreApiService.getMoviesGenreIds() } returns Response.success(GenreFixture.emptyGenresResponse)
        val response = genreRepository.getAllGenres()
        assert(response.isSuccess)
        assertEquals(emptyList<GenresResponse>(), response.getOrThrow())
    }

    @Test
    fun getGenres_apiReturnGenres_returnGenres() = runTest {
        val genres = GenreFixture.genresResponse(5)
        coEvery { genreApiService.getMoviesGenreIds() } returns Response.success(genres)
        val response = genreRepository.getAllGenres()
        assert(response.isSuccess)
        assertEquals(genres.genres.map { it.asGenre() }, response.getOrThrow())
    }

    @Test
    fun getGenres_apiReturnError_exceptionOccurred() = runTest {
        coEvery { genreApiService.getMoviesGenreIds() } returns errorResponse(400)
        val result = genreRepository.getAllGenres()
        assert(result.isFailure)
        assertEquals(null, result.getOrNull())
    }

    @Test
    fun getGenres_apiReturnException_returnFailResult() = runTest {
        val runtimeException = RuntimeException("Network Failure")
        coEvery { genreApiService.getMoviesGenreIds() } throws runtimeException
        val response = genreRepository.getAllGenres()
        assert(response.isFailure)
        assertEquals(null, response.getOrNull())
        assertEquals(ApiException.UnknownException("Network Failure"), response.exceptionOrNull())
    }

}