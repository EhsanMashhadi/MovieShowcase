package software.ehsan.movieshowcase.core.database

import software.ehsan.movieshowcase.core.database.model.MovieEntity
import software.ehsan.movieshowcase.core.model.Movie

fun MovieEntity.asDomain(isBookmarked: Boolean = false): Movie {
    return Movie(
        id = this.id,
        title = this.title,
        voteAverage = this.voteAverage,
        posterPath = this.posterPath,
        overview = this.overview,
        isBookmarked = isBookmarked,
        genres = null
    )
}

fun Movie.asEntity(): MovieEntity {
    return MovieEntity(
        id = this.id,
        title = this.title,
        voteAverage = this.voteAverage,
        posterPath = this.posterPath,
        overview = this.overview
    )
}