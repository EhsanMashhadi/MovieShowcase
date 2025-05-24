package software.ehsan.movieshowcase.core.network.mapper

import software.ehsan.movieshowcase.core.model.Genre
import software.ehsan.movieshowcase.core.network.model.GenreResponse

fun GenreResponse.asGenre() = Genre(
    id = this.id,
    name = this.name
)