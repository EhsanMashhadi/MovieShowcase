package software.ehsan.movieshowcase.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import software.ehsan.movieshowcase.core.designsystem.theme.MovieShowcaseTheme

@Composable
fun ExpandableText(
    text: String,
    textStyle: SpanStyle,
    expandingText: String,
    expandingTextStyle: SpanStyle,
    modifier: Modifier = Modifier,
    charCount: Int = 200
) {
    var isExpanded by remember { mutableStateOf(text.length <= charCount) }
    Text(
        text = buildAnnotatedString {
            if (isExpanded) {
                withStyle(style = textStyle) {
                    append(text)
                }
            } else {
                withStyle(style = textStyle) {
                    append(text.substring(0, charCount - 1) + "... ")
                }
                withStyle(style = expandingTextStyle) {
                    append(expandingText)
                }
            }
        },
        modifier = modifier.clickable {
            if (!isExpanded) {
                isExpanded = true
            }
        })
}

@Preview
@Composable
fun PreviewExpandableText() {
    MovieShowcaseTheme {
        Surface {
            ExpandableText(
                text = "123456789".repeat(100),
                expandingText = "See more",
                textStyle = SpanStyle(color = MaterialTheme.colorScheme.onBackground),
                expandingTextStyle = SpanStyle(
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}