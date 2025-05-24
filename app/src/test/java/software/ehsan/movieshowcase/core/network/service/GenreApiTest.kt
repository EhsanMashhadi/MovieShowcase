package software.ehsan.movieshowcase.core.network.service

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import software.ehsan.movieshowcase.core.network.model.GenresResponse
import software.ehsan.movieshowcase.core.network.service.api.GenreApiService
import software.ehsan.movieshowcase.fixtures.GenreFixture

class GenreApiTest {

    @Test
    fun getGenres_apiReturnsSuccess_returnExpectedGenres() = runTest {
        val expectedGenres = GenreFixture.genres
        val genreApiService: GenreApiService = mockk()
        coEvery { genreApiService.getMoviesGenreIds() } returns Response.success(expectedGenres)
        val actualGenres = genreApiService.getMoviesGenreIds()
        assertTrue(actualGenres.isSuccessful)
        assertEquals(expectedGenres, actualGenres.body())
    }

    @Test
    fun getGenres_apiReturnsError_returnExpectedError() = runTest {
        val genreApiService: GenreApiService = mockk()
        val errorResponse = Response.error<GenresResponse>(
            500,
            "{\"message\":\"Internal Server Error\"}".toResponseBody(null)
        )
        coEvery { genreApiService.getMoviesGenreIds() } returns errorResponse
        val actualGenres = genreApiService.getMoviesGenreIds()
        assertFalse(actualGenres.isSuccessful)
    }
}