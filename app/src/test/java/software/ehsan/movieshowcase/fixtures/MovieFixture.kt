package software.ehsan.movieshowcase.fixtures

import software.ehsan.movieshowcase.core.database.asEntity
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.core.model.Movies
import software.ehsan.movieshowcase.core.network.model.MovieResponse
import software.ehsan.movieshowcase.core.network.model.MoviesResponse

object MovieFixture {
    fun movie(size: Int, isBookmarked: Boolean = false) = List(size) { i ->
        Movie(
            id = i,
            title = "title $i",
            overview = "overview $i",
            posterPath = "poster path $i",
            isBookmarked = isBookmarked,
            genres = null,
            voteAverage = i.mod(5).toFloat()
        )
    }

    fun movies(
        size: Int,
        isBookmarked: Boolean = false,
        totalPage: Int = 1000,
        totalResultsCount: Int = totalPage * size
    ) =
        Movies(
            page = 1,
            results = movie(size, isBookmarked = isBookmarked),
            totalPages = totalPage,
            totalResultsCount = totalResultsCount,
        )

    fun moviesEntity(size: Int) = List(size) { i ->
        Movie(
            id = i,
            title = "title $i",
            overview = "overview $i",
            posterPath = "poster path $i",
            genres = null,
            voteAverage = i.mod(5).toFloat()
        ).asEntity()
    }

    val emptyMovieResponse = MoviesResponse(1, emptyList(), 1, 0)
    val fiveMoviesResponse = MoviesResponse(1, moviesResponse(5), 1, 5)
    fun moviesResponse(size: Int, hasGenre: Boolean = false) = List(size) { i ->
        MovieResponse(
            id = i,
            title = "title $i",
            overview = "overview $i",
            posterPath = "poster path $i",
            genres = if (hasGenre) listOf(i) else null,
            voteAverage = i.mod(5).toFloat()
        )
    }
}