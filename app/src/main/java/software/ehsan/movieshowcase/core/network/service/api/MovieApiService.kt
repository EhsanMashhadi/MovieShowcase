package software.ehsan.movieshowcase.core.network.service.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import software.ehsan.movieshowcase.core.network.model.MovieResponse
import software.ehsan.movieshowcase.core.network.model.MoviesResponse

interface MovieApiService {
    @GET("movie/top_rated")
    suspend fun getTopMovies(): Response<MoviesResponse>

    @GET("discover/movie")
    suspend fun getLatestMovies(
        @Query("sort_by") sortBy: String = "primary_release_date.desc",
        @Query("primary_release_date.lte") releaseDateLte: String?,
        @Query("with_genres") genreId: Int?
    ): Response<MoviesResponse>

    @GET("movie/{movieId}")
    suspend fun getMovieDetails(@Path("movieId") movieId: Int): Response<MovieResponse>

    @GET("search/movie")
    suspend fun search(@Query("query") query: String): Response<MoviesResponse>
}