package software.ehsan.movieshowcase.core.designsystem.theme

import androidx.activity.ComponentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    background = Color.White,
    onBackground = Color.Black,
    primary = Yellow,
    onPrimary = Color.Black,
    tertiary = Yellow,
    onSecondary = DarkGrey900,
    surfaceVariant = DarkGrey850,
    onSurfaceVariant = Color.White
)

private val DarkColorScheme = darkColorScheme(
    background = Color.Black,
    onBackground = Color.White,
    primary = Yellow,
    tertiary = Yellow,
    onSecondary = LightGrey,
    surfaceVariant = DarkGrey850,
    onSurfaceVariant = Color.White
)

@Composable
fun MovieShowcaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    val window = (view.context as ComponentActivity).window
    DisposableEffect(view, window, darkTheme) {
        val insetsController = WindowCompat.getInsetsController(window, view)
        insetsController.isAppearanceLightStatusBars = !darkTheme
        insetsController.isAppearanceLightNavigationBars = !darkTheme
        onDispose {}
    }
    MaterialTheme(
        colorScheme = colorScheme, typography = MovieShowCaseTypography
    ) {
        CompositionLocalProvider(LocalSpacing provides MovieShowcaseSpacing) {
            content()
        }
    }
}

