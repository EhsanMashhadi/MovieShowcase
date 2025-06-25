package software.ehsan.movieshowcase.core.network.mapper

import software.ehsan.movieshowcase.core.data.repository.GenresRepository
import software.ehsan.movieshowcase.core.database.model.MovieEntity
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.core.model.Movies
import software.ehsan.movieshowcase.core.network.model.MovieResponse
import software.ehsan.movieshowcase.core.network.model.MoviesResponse
import software.ehsan.movieshowcase.util.getImageUrl


suspend fun MoviesResponse.asDomain(genreRepository: GenresRepository): Movies {
    val genresMappingResult = genreRepository.getGenreMapping()
    return Movies(
        page = this.page,
        totalPages = this.totalPages,
        totalResults = this.totalResults,
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