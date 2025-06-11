package software.ehsan.movieshowcase.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import software.ehsan.movieshowcase.core.designsystem.theme.MovieShowcaseTheme
import software.ehsan.movieshowcase.core.navigation.AppBottomNavigation
import software.ehsan.movieshowcase.core.navigation.AppNavHost
import software.ehsan.movieshowcase.core.navigation.topLevelRoutes
import software.ehsan.movieshowcase.core.setting.UserSettingViewModel


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userSettingViewModel: UserSettingViewModel = hiltViewModel()
            val userSetting = userSettingViewModel.userSettingState.collectAsStateWithLifecycle()
            val navController = rememberNavController()
            MovieShowcaseTheme(darkTheme = userSetting.value.isDarkMode) {
                Scaffold(bottomBar = {
                    AppBottomNavigation(
                        navController = navController, topLevelRoutes = topLevelRoutes
                    )
                }, content = { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        AppNavHost(
                            navController,
                            isDarkTheme = userSetting.value.isDarkMode,
                            toggleTheme = { userSettingViewModel.toggleTheme() })
                    }

                })
            }
        }
    }
}