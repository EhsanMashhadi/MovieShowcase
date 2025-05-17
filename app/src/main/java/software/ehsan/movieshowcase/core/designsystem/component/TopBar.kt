package software.ehsan.movieshowcase.core.designsystem.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopAppBar(
    modifier: Modifier = Modifier,
    title: AnnotatedString? = null,
    color: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    actionItem: @Composable RowScope.() -> Unit = {},
    navigationIcon: @Composable () -> Unit = {}
) {
    TopAppBar(
        modifier = modifier,
        colors = color,
        title = { title?.let { Text(it, style = MaterialTheme.typography.headlineLarge) } },
        actions = actionItem,
        navigationIcon = navigationIcon,
    )
}
