package software.ehsan.movieshowcase.feature.dashboard

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import software.ehsan.movieshowcase.R
import software.ehsan.movieshowcase.core.designsystem.component.AppTopAppBar
import software.ehsan.movieshowcase.core.designsystem.component.CenteredLoading
import software.ehsan.movieshowcase.core.designsystem.component.MovieCard
import software.ehsan.movieshowcase.core.designsystem.component.MovieDetailsCard
import software.ehsan.movieshowcase.core.designsystem.theme.MovieShowcaseTheme
import software.ehsan.movieshowcase.core.designsystem.theme.spacing
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.core.model.Movies

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    goToDetail: (movieId: Int) -> Unit,
    isDarkTheme: Boolean = false,
    toggleTheme: () -> Unit = {}
) {
    val dashboardState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSuccess = dashboardState is DashboardState.Success

    DashboardScaffold(isDarkTheme = isDarkTheme, toggleTheme = toggleTheme, isSuccess = isSuccess) {
        DashboardContent(
            dashboardState,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) { goToDetail(it) }
    }
    LaunchedEffect(Unit) {
        viewModel.handleIntent(DashboardIntent.LoadAllMovies)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardScaffold(
    isDarkTheme: Boolean = false,
    toggleTheme: () -> Unit = {},
    isSuccess: Boolean = true,
    content: @Composable (PaddingValues) -> Unit
) {
    val text = if (isSuccess) buildAnnotatedString {
        append(stringResource(R.string.dashboard_topFiveTitle))
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.tertiary)) {
            append(stringResource(R.string.all_dot))
        }
    } else buildAnnotatedString { append(stringResource(R.string.app_name)) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            DashboardTopBar(text = text, isDarkTheme = isDarkTheme, toggleTheme = toggleTheme)
        }) { innerPadding -> content(innerPadding) }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar(text: AnnotatedString, isDarkTheme: Boolean, toggleTheme: () -> Unit) {
    AppTopAppBar(
        title = text,
        actionItem = {
            val imageVector =
                if (isDarkTheme) Icons.Filled.DarkMode else Icons.Filled.LightMode
            IconButton(onClick = { toggleTheme() }) {
                Icon(
                    imageVector = imageVector,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = if (isDarkTheme) stringResource(R.string.contentDescription_darkTheme) else stringResource(
                        R.string.contentDescription_lightTheme
                    )
                )
            }
        },
        color = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun DashboardContent(
    dashboardState: DashboardState,
    modifier: Modifier = Modifier,
    goToDetail: (movieId: Int) -> Unit = {}
) {
    when (dashboardState) {
        is DashboardState.Idle -> {}
        is DashboardState.Loading -> {
            CenteredLoading()
        }

        is DashboardState.Success -> {
            DashboardSuccessContent(
                topMovies = dashboardState.topMovies,
                latestMovie = dashboardState.latestMovie,
                modifier = modifier,
                goToDetail = goToDetail
            )
        }

        is DashboardState.Error -> {
            DashboardErrorContent()
        }
    }
}

@Composable
private fun DashboardSuccessContent(
    topMovies: Movies?,
    latestMovie: Movie?,
    modifier: Modifier = Modifier,
    goToDetail: (movieId: Int) -> Unit
) {
    val lazyColumnState = rememberLazyListState()
    LazyColumn(
        state = lazyColumnState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = MaterialTheme.spacing.l,
            vertical = MaterialTheme.spacing.xxl
        ),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxl)
    ) {
        topMovies?.let {
            item {
                TopFiveList(items = it.results, goToDetail = goToDetail)
            }
        }
        latestMovie?.let {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val headline = buildAnnotatedString {
                        append(stringResource(R.string.all_latest))
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.tertiary)) {
                            append(stringResource(R.string.all_dot))
                        }
                    }
                    Text(
                        headline,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        stringResource(R.string.all_seeMore),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.clickable { })
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.l))
                Latest(item = it, goToDetail = goToDetail)
            }
        }
    }
}

@Composable
private fun DashboardErrorContent() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            stringResource(R.string.all_error),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = MaterialTheme.spacing.m),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun TopFiveList(items: List<Movie>, goToDetail: (movieId: Int) -> Unit = {}) {
    val lazyRowState = rememberLazyListState()
    LazyRow(
        state = lazyRowState,
        modifier = Modifier.testTag("list"),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xl)
    ) {
        itemsIndexed(items = items) { index, item ->
            Log.d("item", item.toString())
            MovieCard(
                item.title,
                item.voteAverage,
                imageUrl = item.posterPath,
                onClick = { goToDetail(item.id) },
                onSave = {},
                genres = item.genres,
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .testTag(item.title)
            )
        }
    }
}

@Composable
private fun Latest(item: Movie, goToDetail: (movieId: Int) -> Unit = {}) {
    MovieDetailsCard(
        item.title,
        item.voteAverage,
        imageUrl = item.posterPath,
        genres = item.genres,
        overview = item.overview,
        onClick = { goToDetail(item.id) },
        onSave = {})
}


@Preview
@Composable
fun PreviewMovieShowcaseContentSuccess() {
    MovieShowcaseTheme {
        Surface {
            DashboardContent(
                dashboardState = DashboardState.Success(
                    topMovies = Movies(
                        1,
                        movies,
                        1,
                        1
                    ), latestMovie = Movie(1, "title 1", "ds", emptyList(), 3.5f, "")
                )
            )
        }
    }
}

@Preview
@Composable
fun PreviewMovieShowcaseContentError() {
    MovieShowcaseTheme {
        Surface {
            DashboardContent(dashboardState = DashboardState.Error(""))
        }
    }
}

val movies = listOf(
    Movie(1, "title 1", "ds", emptyList(), 3.5f, ""),
    Movie(2, "title 2", "ds", emptyList(), 2.5f, ""),
    Movie(3, "title 3", "ds", emptyList(), 1.5f, ""),
    Movie(4, "title 4", "ds", emptyList(), 4.5f, ""),
)