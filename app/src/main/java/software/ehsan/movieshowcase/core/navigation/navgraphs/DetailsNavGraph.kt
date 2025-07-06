package software.ehsan.movieshowcase.core.navigation.navgraphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import software.ehsan.movieshowcase.core.navigation.DetailsNavGraphScreens
import software.ehsan.movieshowcase.feature.detail.DetailScreen

fun NavGraphBuilder.detailNavGraph(navController: NavController) {
    composable<DetailsNavGraphScreens.MovieDetails> { backStackEntry ->
        val movie: DetailsNavGraphScreens.MovieDetails = backStackEntry.toRoute()
        DetailScreen(movieId = movie.movieId, onBack = { navController.popBackStack() })
    }
}