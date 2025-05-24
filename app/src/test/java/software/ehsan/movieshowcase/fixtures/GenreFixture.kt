package software.ehsan.movieshowcase.fixtures

import software.ehsan.movieshowcase.core.model.Genre
import software.ehsan.movieshowcase.core.network.model.GenreResponse
import software.ehsan.movieshowcase.core.network.model.GenresResponse

object GenreFixture {
    val genres = GenresResponse(genreResponse(10))
    fun genreResponse(count: Int) = List(count) { i ->
        GenreResponse(i, "Genre $i")
    }

    fun genres(count: Int) = List(count) {
        Genre(
            id = it,
            name = "Genre $it"
        )
    }

    val emptyGenresResponse = GenresResponse(genres = emptyList())
    fun genresResponse(size: Int) =
        GenresResponse(
            genreResponse(size)
        )
}