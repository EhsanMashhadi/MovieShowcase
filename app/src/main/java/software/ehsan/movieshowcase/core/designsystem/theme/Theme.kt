package software.ehsan.movieshowcase.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    background = Color.White,
    onBackground = Color.Black,
    primary = Yellow,
    tertiary = Yellow,
    onSecondary = DarkGrey
)

private val DarkColorScheme = darkColorScheme(
    background = Color.Black,
    onBackground = Color.White,
    primary = Yellow,
    tertiary = Yellow,
    onSecondary = LightGrey
)

@Composable
fun MovieShowcaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MovieShowCaseTypography
    ) {
        CompositionLocalProvider(LocalSpacing provides MovieShowcaseSpacing) {
            content()
        }
    }
}

