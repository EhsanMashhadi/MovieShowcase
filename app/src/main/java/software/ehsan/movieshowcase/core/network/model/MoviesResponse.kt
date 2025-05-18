package software.ehsan.movieshowcase.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MoviesResponse(
    @Json(name = "page") val page: Int,
    @Json(name = "results") val results: List<MovieResponse>,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalResults: Int
)

@JsonClass(generateAdapter = true)
data class MovieResponse(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "genre_ids") val genres: List<Int>?,
    @Json(name = "vote_average") val voteAverage: Float,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "overview") val overview: String
)