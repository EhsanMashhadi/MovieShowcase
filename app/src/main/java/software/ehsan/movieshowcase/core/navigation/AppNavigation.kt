package software.ehsan.movieshowcase.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import software.ehsan.movieshowcase.R
import software.ehsan.movieshowcase.feature.bookmark.BookmarkScreen
import software.ehsan.movieshowcase.feature.dashboard.DashboardScreen
import software.ehsan.movieshowcase.feature.detail.DetailScreen
import software.ehsan.movieshowcase.feature.latest.LatestScreen
import software.ehsan.movieshowcase.feature.search.SearchScreen

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
                goToLatest = {
                    navController.navigateSafe(route = Screens.Latest)
                },
                isDarkTheme = isDarkTheme,
                toggleTheme = toggleTheme
            )
        }
        composable<Screens.MovieDetails> { backStackEntry ->
            val movie: Screens.MovieDetails = backStackEntry.toRoute()
            DetailScreen(movieId = movie.movieId, onBack = { navController.popBackStack() })
        }
        composable<Screens.Latest> { backStackEntry ->
            LatestScreen(onBack = { navController.popBackStack() }, onGoToDetails = {
                navController.navigateSafe(
                    route = Screens.MovieDetails(it.id)
                )
            })
        }
        composable<Screens.Bookmarks> {
            BookmarkScreen(onBack = { navController.popBackStack() }, onGoToDetails = {
                navController.navigateSafe(
                    route = Screens.MovieDetails(it.id)
                )
            })
        }

        composable<Screens.Search> {
            SearchScreen()
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

data class TopLevelRoute(val icon: ImageVector, @StringRes val label: Int, val destination: Screens)

val topLevelRoutes = listOf(
    TopLevelRoute(
        icon = Icons.Default.Home,
        label = R.string.all_dashboard,
        destination = Screens.Dashboard
    ),
    TopLevelRoute(
        icon = Icons.Default.Search,
        label = R.string.all_search,
        destination = Screens.Search
    ),
    TopLevelRoute(
        icon = Icons.Default.Bookmarks,
        label = R.string.all_bookmarks,
        destination = Screens.Bookmarks
    )
)


@Composable
fun AppBottomNavigation(navController: NavHostController, topLevelRoutes: List<TopLevelRoute>) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.value?.destination
    NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
        topLevelRoutes.forEach { topLevelRoute ->
            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.background,
                    unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                    indicatorColor = MaterialTheme.colorScheme.primary
                ),
                selected = currentDestination?.hierarchy?.any { it.hasRoute(topLevelRoute.destination::class) } == true,
                icon = {
                    Icon(
                        imageVector = topLevelRoute.icon,
                        stringResource(topLevelRoute.label)
                    )
                },
                onClick = {
                    navController.navigate(topLevelRoute.destination) {
                        launchSingleTop = true
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                    }
                })
        }
    }
}