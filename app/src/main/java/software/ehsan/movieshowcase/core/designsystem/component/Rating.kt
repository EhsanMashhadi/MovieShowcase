package software.ehsan.movieshowcase.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import software.ehsan.movieshowcase.R
import software.ehsan.movieshowcase.core.designsystem.theme.MovieShowcaseTheme
import software.ehsan.movieshowcase.core.designsystem.theme.spacing

@Composable
fun Rating(
    rating: Float,
    maxValue: Int,
    modifier: Modifier = Modifier,
    fullStarIcon: Painter = painterResource(R.drawable.star_full),
    halfStarIcon: Painter = painterResource(R.drawable.star_half),
    emptyStarIcon: Painter = painterResource(R.drawable.star_empty),
    starSize: Dp = MaterialTheme.spacing.l,
    startSpacing: Dp = MaterialTheme.spacing.xs,
    starTint: Color = Color.Unspecified
) {
    Row(modifier.semantics {
        contentDescription = "Movie rating: $rating out of $maxValue"
    }) {
        for (i in 1..maxValue) {
            val icon = when {
                i <= rating -> {
                    fullStarIcon
                }

                i > rating && i < rating + 1 -> {
                    halfStarIcon
                }

                else -> {
                    emptyStarIcon
                }
            }
            Icon(painter = icon, null, tint = starTint, modifier = Modifier.width(starSize))
            if (i < maxValue) {
                Spacer(modifier = Modifier.width(startSpacing))
            }
        }
    }
}

@Preview
@Composable
fun PreviewRating() {
    MovieShowcaseTheme {
        Surface {
            Column {
                Rating(1.5f, 5)
                Rating(2.0f, 5)
                Rating(3.0f, 5)
                Rating(3.5f, 5)
                Rating(4.0f, 5)
                Rating(5.0f, 5)
            }
        }
    }
}