package software.ehsan.movieshowcase.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenresResponse(@Json(name = "genres") val genres: List<GenreResponse>)

@JsonClass(generateAdapter = true)
data class GenreResponse(@Json(name = "id") val id: Int, @Json(name = "name") val name: String)