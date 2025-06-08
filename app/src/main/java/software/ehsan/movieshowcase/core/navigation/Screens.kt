package software.ehsan.movieshowcase.core.navigation

import kotlinx.serialization.Serializable


sealed class Screens {
    @Serializable
    data class MovieDetails(val movieId: Int) : Screens()

    @Serializable
    data object Dashboard : Screens()

    @Serializable
    data object Latest : Screens()

    @Serializable
    data object Bookmarks : Screens()

    @Serializable
    data object Search : Screens()
}