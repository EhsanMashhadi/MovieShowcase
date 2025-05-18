package software.ehsan.movieshowcase.fixtures

import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.core.model.Movies

object MovieFixture {
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

    fun movies(size: Int) =
        Movies(
            page = 1,
            results = movie(size),
            totalPages = 1,
            totalResults = size,
        )
}