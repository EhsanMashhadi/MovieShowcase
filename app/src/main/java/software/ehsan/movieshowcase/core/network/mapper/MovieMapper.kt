package software.ehsan.movieshowcase.core.network.mapper

import software.ehsan.movieshowcase.core.cache.MovieEntity
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.core.model.Movies
import software.ehsan.movieshowcase.core.network.model.MovieResponse
import software.ehsan.movieshowcase.core.network.model.MoviesResponse
import software.ehsan.movieshowcase.util.getImageUrl


fun MoviesResponse.asDomain(genresName: List<String>?) = Movies(
    page = this.page,
    totalPages = this.totalPages,
    totalResults = this.totalResults,
    results = this.results.map { it.asDomain(genresName) })

fun MovieResponse.asDomain(genresName: List<String>?) = Movie(
    id = this.id,
    title = this.title,
    voteAverage = this.voteAverage,
    posterPath = getImageUrl(imageUrl = this.posterPath),
    overview = this.overview,
    genres = genresName
)

fun MovieEntity.asDomain() = Movie(
    id = this.id,
    title = this.title,
    voteAverage = this.voteAverage,
    posterPath = getImageUrl(imageUrl = this.posterPath),
    overview = this.overview,
    genres = genres
)