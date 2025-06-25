package software.ehsan.movieshowcase.core.network.mapper

import software.ehsan.movieshowcase.core.data.repository.GenreRepository
import software.ehsan.movieshowcase.core.database.model.MovieEntity
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.core.model.Movies
import software.ehsan.movieshowcase.core.network.model.MovieResponse
import software.ehsan.movieshowcase.core.network.model.MoviesResponse
import software.ehsan.movieshowcase.util.getImageUrl


suspend fun MoviesResponse.asDomain(genreRepository: GenreRepository): Movies {
    val genresMappingResult = genreRepository.getGenresMapping()
    return Movies(
        page = this.page,
        totalPages = this.totalPages,
        totalResultsCount = this.totalResults,
        results = this.results.map { it.asDomain(it.genres?.mapNotNull { genresMappingResult[it] }) })
}

fun MovieResponse.asDomain(genres: List<String>?, isBookmarked: Boolean = false) =
    Movie(
        id = this.id,
        title = this.title,
        voteAverage = this.voteAverage,
        posterPath = getImageUrl(imageUrl = this.posterPath),
        overview = this.overview,
        isBookmarked = isBookmarked,
        genres = genres
    )

fun MovieEntity.asDomain() = Movie(
    id = this.id,
    title = this.title,
    voteAverage = this.voteAverage,
    posterPath = getImageUrl(imageUrl = this.posterPath),
    overview = this.overview,
    genres = null
)