package software.ehsan.movieshowcase.core.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import software.ehsan.movieshowcase.feature.dashboard.DashboardScreen
import software.ehsan.movieshowcase.feature.detail.DetailScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    isDarkTheme: Boolean = false,
    toggleTheme: () -> Unit = {}
) {
    NavHost(navController = navController, startDestination = Screens.Dashboard) {
        composable<Screens.Dashboard> { backStackEntry ->
            DashboardScreen(
                goToDetail = {
                    navController.navigateSafe(route = Screens.MovieDetails(it))
                },
                isDarkTheme = isDarkTheme,
                toggleTheme = toggleTheme
            )
        }
        composable<Screens.MovieDetails> { backStackEntry ->
            val movie: Screens.MovieDetails = backStackEntry.toRoute()
            DetailScreen(movieId = movie.movieId, onBack = { navController.popBackStack() })
        }
    }
}

fun <T : Any> NavController.navigateSafe(
    route: T,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    if (this.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        this.navigate(route, navOptions, navigatorExtras)
    }
}