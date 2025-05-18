package software.ehsan.movieshowcase.core.network.service.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import software.ehsan.movieshowcase.core.network.model.MovieResponse
import software.ehsan.movieshowcase.core.network.model.MoviesResponse

interface MovieApiService {
    @GET("movie/top_rated")
    suspend fun getTopMovies(): Response<MoviesResponse>

    @GET("movie/now_playing")
    suspend fun getLatestMovie(): Response<MoviesResponse>

    @GET("movie/{movieId}")
    suspend fun getMovieDetails(@Path("movieId") movieId: Int): Response<MovieResponse>
}