package software.ehsan.movieshowcase.core.cache.mapper

import software.ehsan.movieshowcase.core.cache.MovieEntity
import software.ehsan.movieshowcase.core.network.model.MovieResponse


fun MovieResponse.asEntity(genres: List<String>?): MovieEntity {
    return MovieEntity(
        id = this.id,
        title = this.title,
        voteAverage = this.voteAverage,
        posterPath = this.posterPath,
        overview = this.overview,
        genres = genres
    )
}