package software.ehsan.movieshowcase.core.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import software.ehsan.movieshowcase.core.navigation.navgraphs.bookmarkNavGraph
import software.ehsan.movieshowcase.core.navigation.navgraphs.dashboardNavGraph
import software.ehsan.movieshowcase.core.navigation.navgraphs.searchNavGraph

@Composable
fun AppNavHost(
    navController: NavHostController,
    isDarkTheme: Boolean = false,
    toggleTheme: () -> Unit = {}
) {
    NavHost(navController = navController, startDestination = Screens.DashboardNavGraph) {
        dashboardNavGraph(
            navController = navController,
            isDarkTheme = isDarkTheme,
            toggleTheme = toggleTheme
        )
        searchNavGraph(navController = navController)
        bookmarkNavGraph(navController = navController)
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

@Composable
fun AppBottomNavigation(navController: NavHostController, topLevelRoutes: List<TopLevelRoute>) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination
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
                        restoreState = true
                    }
                })
        }
    }
}