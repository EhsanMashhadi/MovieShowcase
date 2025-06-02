package software.ehsan.movieshowcase.core.model

data class Movies(
    val page: Int,
    val results: List<Movie>,
    val totalPages: Int,
    val totalResults: Int
)

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val genres: List<String>?,
    val voteAverage: Float,
    val posterPath: String?,
    val isBookmarked: Boolean = false
)