package software.ehsan.movieshowcase.util.fixtures

import software.ehsan.movieshowcase.core.model.Movie


fun movie(size: Int) = List(size) { i ->
    Movie(
        id = i,
        title = "title $i",
        overview = "overview $i",
        posterPath = "poster path $i",
        genres = null,
        voteAverage = i.mod(5).toFloat()
    )
}