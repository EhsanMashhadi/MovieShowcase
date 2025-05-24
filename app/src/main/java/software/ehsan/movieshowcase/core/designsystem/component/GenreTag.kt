package software.ehsan.movieshowcase.core.designsystem.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import software.ehsan.movieshowcase.core.designsystem.theme.MovieShowcaseTheme
import software.ehsan.movieshowcase.core.designsystem.theme.spacing
import software.ehsan.movieshowcase.core.model.Genre
import java.util.Locale

@Composable
fun GenreTag(
    genre: Genre,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    number: Int? = null,
    onClick: (Genre) -> Unit
) {
    val backgroundColor =
        if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor =
        if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = modifier
            .wrapContentWidth()
            .background(
                backgroundColor,
                shape = MaterialTheme.shapes.medium
            )
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick(genre) }
            .animateContentSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .padding(
                    horizontal = MaterialTheme.spacing.l,
                    vertical = MaterialTheme.spacing.xs
                )
        ) {
            Text(
                genre.name.uppercase(Locale.getDefault()),
                color = textColor,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(number?.let { " ($it)" } ?: "",
                color = textColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@Preview(name = "Actionable Tag")
@Composable
fun PreviewActionableTagWithNumber() {
    MovieShowcaseTheme {
        Surface {
            var isAction by remember { mutableStateOf(true) }
            GenreTag(
                Genre(1, "action"),
                isActive = isAction,
                number = 10,
                onClick = { isAction = !isAction })
        }
    }
}

@Preview(name = "Inactive Tag")
@Composable
fun PreviewInactiveTagWithoutNumber() {
    MovieShowcaseTheme {
        Surface {
            GenreTag(Genre(1, "Adventure"), isActive = false, onClick = {})
        }
    }
}