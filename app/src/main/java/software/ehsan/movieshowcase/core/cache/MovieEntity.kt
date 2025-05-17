package software.ehsan.movieshowcase.core.cache


data class MovieEntity(
    val id: Int,
    val title: String,
    val genres: List<String>?,
    val voteAverage: Float,
    val posterPath: String?,
    val overview: String
)