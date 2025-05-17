package software.ehsan.movieshowcase.core.network.service.api

import retrofit2.Response
import retrofit2.http.GET
import software.ehsan.movieshowcase.core.network.model.GenresResponse

interface GenreApiService {

    @GET("genre/movie/list")
    suspend fun getMoviesGenreIds(): Response<GenresResponse>
}