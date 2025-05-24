package software.ehsan.movieshowcase.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import software.ehsan.movieshowcase.core.designsystem.theme.MovieShowcaseTheme
import software.ehsan.movieshowcase.core.designsystem.theme.ThemePreviews
import software.ehsan.movieshowcase.core.designsystem.theme.spacing
import java.util.Locale


private fun getDisplayRating(rating: Float): String {
    val movieRating = rating / 2
    return String.format(Locale.getDefault(), "%.1f", movieRating)
}

@Composable
private fun MovieImageWithSaveIcon(
    imageUrl: String?,
    isSaved: Boolean,
    contentDescription: String,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        val icon =
            ImageVector.vectorResource(id = if (isSaved) MovieShowcaseIcons.SaveFilledIcon else MovieShowcaseIcons.SaveIcon)
        Icon(
            imageVector = icon,
            contentDescription = if (isSaved) "Saved icon" else "Save icon",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = MaterialTheme.spacing.l, top = MaterialTheme.spacing.l)
                .semantics {
                    onClick(if (isSaved) "Remove from saved" else "Save movie") {
                        onSave()
                        true
                    }
                    role = Role.Button
                }
                .clickable { onSave() },
            tint = Color.Unspecified
        )
    }
}

@Composable
fun DisplayRating(rating: Float) {
    val ratingFive = remember(rating) { rating / 2 }
    val displayingRating = remember(ratingFive) { getDisplayRating(rating) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(displayingRating, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.s))
        Rating(ratingFive, 5, modifier = Modifier.height(MaterialTheme.spacing.l))
    }
}

@Composable
fun MovieCard(
    title: String,
    rating: Float,
    imageUrl: String?,
    genres: List<String>?,
    modifier: Modifier = Modifier,
    isSaved: Boolean = false,
    portrait: Boolean = false,
    onSave: () -> Unit,
    onClick: () -> Unit
) {
    val displayedRating = getDisplayRating(rating)
    val aspectRatio = if (portrait) 2f / 3f else 3f / 2f
    val maxLine = if (portrait) 2 else 1
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClickLabel = "View movie details for $title") { onClick() }
            .semantics {
                contentDescription = "Movie card for $title rating $displayedRating"
            }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio),
            border = CardDefaults.outlinedCardBorder()
        ) {
            MovieImageWithSaveIcon(
                imageUrl = imageUrl,
                isSaved = isSaved,
                onSave = onSave,
                contentDescription = "Movie poster for $title"
            )
        }
        Spacer(Modifier.height(MaterialTheme.spacing.m))
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            maxLines = maxLine,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(MaterialTheme.spacing.xs))
        DisplayRating(rating = rating)
        genres?.let {
            Text(
                it.joinToString(", "),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.height(MaterialTheme.spacing.xs))
    }
}

@Composable
fun MovieDetailsCard(
    title: String,
    rating: Float,
    imageUrl: String?,
    genres: List<String>?,
    overview: String,
    modifier: Modifier = Modifier,
    isSaved: Boolean = false,
    onSave: () -> Unit,
    onClick: () -> Unit
) {
    val displayedRating = getDisplayRating(rating)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClickLabel = "View movie details for $title") { onClick() }
            .semantics {
                contentDescription = "Movie card for $title rating $displayedRating"
            }) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.5f)
        ) {
            MovieImageWithSaveIcon(
                imageUrl = imageUrl,
                isSaved = isSaved,
                onSave = onSave,
                contentDescription = "Movie poster for $title"
            )
        }
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.l))
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.5f)
                .padding(vertical = MaterialTheme.spacing.m)
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))
            DisplayRating(rating = rating)
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))
            Text(
                genres?.joinToString(", ") ?: "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))
            Text(
                overview,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondary,
                maxLines = 6,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@ThemePreviews
@Composable
fun PreviewMovieCard() {
    MovieShowcaseTheme {
        Surface {
            Column {
                MovieCard(
                    "Hitman’s Wife’s Bodyguard",
                    7.0f,
                    imageUrl = "/9cqNxx0GxF0bflZmeSMuL5tnGzr.jpg",
                    isSaved = true,
                    onSave = {},
                    genres = listOf("comedy", "crime"),
                    onClick = {})
            }

        }
    }
}

@ThemePreviews
@Composable
fun PreviewMovieDetailsCard() {
    MovieShowcaseTheme {
        Surface {
            Column {
                MovieDetailsCard(
                    "Hitman’s Wife’s Bodyguard",
                    7.0f,
                    imageUrl = "/9cqNxx0GxF0bflZmeSMuL5tnGzr.jpg",
                    isSaved = true,
                    genres = listOf("comedy", "crime"),
                    overview = "long text ".repeat(1) + "end.",
                    onSave = {},
                    onClick = {})
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.l))
                MovieDetailsCard(
                    "Short Title",
                    4.5f,
                    imageUrl = "",
                    isSaved = false,
                    genres = listOf("action", "thriller", "another genre"),
                    overview = "short overview.",
                    onSave = {},
                    onClick = {})
            }

        }
    }
}