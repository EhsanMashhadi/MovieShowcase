package software.ehsan.movieshowcase.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import software.ehsan.movieshowcase.R

data class TopLevelRoute(val icon: ImageVector, @StringRes val label: Int, val destination: Screens)

val topLevelRoutes = listOf(
    TopLevelRoute(
        icon = Icons.Default.Home,
        label = R.string.all_dashboard,
        destination = Screens.DashboardNavGraph
    ),
    TopLevelRoute(
        icon = Icons.Default.Search,
        label = R.string.all_search,
        destination = Screens.SearchNavGraph
    ),
    TopLevelRoute(
        icon = Icons.Default.Bookmarks,
        label = R.string.all_bookmarks,
        destination = Screens.BookmarksNavGraph
    )
)