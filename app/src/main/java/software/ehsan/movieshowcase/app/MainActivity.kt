package software.ehsan.movieshowcase.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import software.ehsan.movieshowcase.core.designsystem.theme.MovieShowcaseTheme
import software.ehsan.movieshowcase.core.navigation.AppBottomNavigation
import software.ehsan.movieshowcase.core.navigation.AppNavHost
import software.ehsan.movieshowcase.core.navigation.topLevelRoutes


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkMode by remember { mutableStateOf(false) }
            val navController = rememberNavController()
            MovieShowcaseTheme(darkTheme = isDarkMode) {
                Scaffold(
                    bottomBar = {
                        AppBottomNavigation(
                            navController = navController,
                            topLevelRoutes = topLevelRoutes
                        )
                    },
                    content = { paddingValues ->
                        Box(modifier = Modifier.padding(paddingValues)) {
                            AppNavHost(
                                navController,
                                isDarkTheme = isDarkMode,
                                toggleTheme = { isDarkMode = !isDarkMode })
                        }

                    })
            }
        }
    }
}