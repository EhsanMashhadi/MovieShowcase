package software.ehsan.movieshowcase.core.navigation.navgraphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import software.ehsan.movieshowcase.core.navigation.DetailsNavGraphScreens
import software.ehsan.movieshowcase.core.navigation.Screens
import software.ehsan.movieshowcase.core.navigation.SearchNavGraphScreens
import software.ehsan.movieshowcase.core.navigation.navigateSafe
import software.ehsan.movieshowcase.feature.search.SearchScreen

fun NavGraphBuilder.searchNavGraph(navController: NavController) {
    navigation<Screens.SearchNavGraph>(startDestination = SearchNavGraphScreens.Search) {
        composable<SearchNavGraphScreens.Search> {
            SearchScreen(onGoToDetails = {
                navController.navigateSafe(
                    route = DetailsNavGraphScreens.MovieDetails(it.id)
                )
            })
        }
        detailNavGraph(navController = navController)
    }
}