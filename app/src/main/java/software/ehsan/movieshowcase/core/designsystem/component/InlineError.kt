package software.ehsan.movieshowcase.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import software.ehsan.movieshowcase.R
import software.ehsan.movieshowcase.core.designsystem.theme.MovieShowcaseTheme
import software.ehsan.movieshowcase.core.designsystem.theme.spacing

@Composable
fun InlineError(error: String, modifier: Modifier = Modifier, onRetry: (() -> Unit)? = null) {
    Row(
        modifier = modifier
            .padding(
                vertical = MaterialTheme.spacing.s
            ), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            error,
            modifier = Modifier.weight(1f).padding(end = MaterialTheme.spacing.s),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge
        )
        onRetry?.let {
            Text(
                text = stringResource(id = R.string.all_retry),
                modifier = Modifier.clickable { onRetry() },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview
@Composable
fun PreviewErrorWithRetry() {
    MovieShowcaseTheme {
        Surface {
            InlineError(error = "error", onRetry = {  })
        }
    }
}

@Preview
@Composable
fun PreviewErrorWithoutAction() {
    MovieShowcaseTheme {
        Surface {
            InlineError(error = "error")
        }
    }
}