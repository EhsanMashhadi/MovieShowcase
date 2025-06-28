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
import software.ehsan.movieshowcase.core.network.model.MoviesResponse
import software.ehsan.movieshowcase.core.network.service.api.MovieApiService
import software.ehsan.movieshowcase.fixtures.MovieFixture

class MoviesApiTest {

    @Test
    fun getTopMovies_returnsSuccess_successResponse() = runTest {
        val expectedMovie = MovieFixture.fiveMoviesResponse
        val movieApi: MovieApiService = mockk()
        coEvery { movieApi.getTopMovies(any()) } returns Response.success(expectedMovie)
        val actualMovies = movieApi.getTopMovies(1)
        assertTrue(actualMovies.isSuccessful)
        assertEquals(expectedMovie, actualMovies.body())
    }

    @Test
    fun getTopMovies_apiReturnsError_unsuccessResponse() = runTest {
        val movieApi: MovieApiService = mockk()
        val errorResponse = Response.error<MoviesResponse>(
            500,
            "{\"message\":\"Internal Server Error\"}".toResponseBody(null)
        )
        coEvery { movieApi.getTopMovies(any()) } returns errorResponse
        val actualMovies = movieApi.getTopMovies(1)
        assertFalse(actualMovies.isSuccessful)
    }
}