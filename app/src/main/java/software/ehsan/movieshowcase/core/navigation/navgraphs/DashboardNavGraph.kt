package software.ehsan.movieshowcase.core.navigation.navgraphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import software.ehsan.movieshowcase.core.navigation.DashboardNavGraphScreens
import software.ehsan.movieshowcase.core.navigation.DetailsNavGraphScreens
import software.ehsan.movieshowcase.core.navigation.Screens
import software.ehsan.movieshowcase.core.navigation.navigateSafe
import software.ehsan.movieshowcase.feature.dashboard.DashboardScreen
import software.ehsan.movieshowcase.feature.latest.LatestScreen

fun NavGraphBuilder.dashboardNavGraph(
    navController: NavController,
    isDarkTheme: Boolean,
    toggleTheme: () -> Unit
) {
    navigation<Screens.DashboardNavGraph>(startDestination = DashboardNavGraphScreens.Dashboard) {
        composable<DashboardNavGraphScreens.Dashboard> { backStackEntry ->
            DashboardScreen(
                goToDetail = {
                    navController.navigateSafe(route = DetailsNavGraphScreens.MovieDetails(it))
                },
                goToLatest = {
                    navController.navigateSafe(route = DashboardNavGraphScreens.Latest)
                },
                isDarkTheme = isDarkTheme,
                toggleTheme = toggleTheme
            )
        }
        composable<DashboardNavGraphScreens.Latest> { backStackEntry ->
            LatestScreen(onBack = { navController.popBackStack() }, onGoToDetails = {
                navController.navigateSafe(
                    route = DetailsNavGraphScreens.MovieDetails(it.id)
                )
            })
        }
        detailNavGraph(navController = navController)
    }
}
