package software.ehsan.movieshowcase.core.navigation.navgraphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import software.ehsan.movieshowcase.core.navigation.BookmarkNavGraphScreens
import software.ehsan.movieshowcase.core.navigation.DetailsNavGraphScreens
import software.ehsan.movieshowcase.core.navigation.Screens
import software.ehsan.movieshowcase.core.navigation.navigateSafe
import software.ehsan.movieshowcase.feature.bookmark.BookmarkScreen

fun NavGraphBuilder.bookmarkNavGraph(navController: NavController) {
    navigation<Screens.BookmarksNavGraph>(startDestination = BookmarkNavGraphScreens.Bookmark) {
        composable<BookmarkNavGraphScreens.Bookmark> {
            BookmarkScreen(onGoToDetails = {
                navController.navigateSafe(
                    route = DetailsNavGraphScreens.MovieDetails(it.id)
                )
            })
        }
        detailNavGraph(navController = navController)
    }
}