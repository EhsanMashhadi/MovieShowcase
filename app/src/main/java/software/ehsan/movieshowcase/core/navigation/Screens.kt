package software.ehsan.movieshowcase.core.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screens {
    @Serializable
    data object DashboardNavGraph : Screens

    @Serializable
    data object SearchNavGraph : Screens

    @Serializable
    data object BookmarksNavGraph : Screens
}

@Serializable
sealed interface DashboardNavGraphScreens {
    @Serializable
    data object Dashboard : DashboardNavGraphScreens

    @Serializable
    data object Latest : DashboardNavGraphScreens
}

@Serializable
sealed interface SearchNavGraphScreens {
    @Serializable
    data object Search : SearchNavGraphScreens
}

@Serializable
sealed interface BookmarkNavGraphScreens {
    @Serializable
    data object Bookmark : BookmarkNavGraphScreens
}

@Serializable
sealed interface DetailsNavGraphScreens {
    @Serializable
    data class MovieDetails(val movieId: Int) : DetailsNavGraphScreens
}