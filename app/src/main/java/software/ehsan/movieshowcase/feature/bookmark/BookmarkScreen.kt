package software.ehsan.movieshowcase.feature.bookmark

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import software.ehsan.movieshowcase.core.designsystem.component.MovieDetailsCard
import software.ehsan.movieshowcase.core.designsystem.theme.MovieShowcaseTheme
import software.ehsan.movieshowcase.core.designsystem.theme.spacing
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.util.fixtures.movie

@Composable
fun BookmarkScreen(
    viewModel: BookmarkViewModel = hiltViewModel(),
    onGoToDetails: (movie: Movie) -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    BookmarkScaffold(
    ) { innerPadding ->
        BookmarkContent(
            uiState,
            onBookmark = { viewModel.handleIntent(BookmarkIntent.BookmarkMovie(it)) },
            onGoToDetail = onGoToDetails,
            paddingValues = innerPadding
        )
    }
}

@Composable
fun BookmarkContent(
    uiState: BookmarkState,
    onBookmark: (Movie) -> Unit,
    onGoToDetail: (Movie) -> Unit,
    paddingValues: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = MaterialTheme.spacing.l)
    ) {

        when (uiState) {
            BookmarkState.Idle -> {}
            BookmarkState.Loading -> {
                CenteredLoading()
            }

            is BookmarkState.Success -> {
                BookmarkSuccess(
                    movies = uiState.bookmarkedMovies,
                    onBookmark = onBookmark,
                    onGoToDetail = onGoToDetail
                )
            }

            is BookmarkState.Error -> {
                BookmarkError()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookmarkScaffold(
    content: @Composable (PaddingValues) -> Unit
) {
    val text = buildAnnotatedString {
        append(stringResource(R.string.bookmarkScreen_appBarTitle))
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.tertiary)) {
            append(stringResource(R.string.all_dot))
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            BookmarkTopBar(text = text)
        }) { innerPadding -> content(innerPadding) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookmarkTopBar(text: AnnotatedString) {
    AppTopAppBar(
        title = text,
        color = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
private fun BookmarkSuccess(
    movies: List<Movie>,
    onBookmark: (Movie) -> Unit,
    onGoToDetail: (Movie) -> Unit,
) {
    if (movies.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(R.string.bookmark_noBookmarkAvailable),
                style = MaterialTheme.typography.bodyLarge
            )
        }

    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.l)) {
            items(movies, key = { movie -> movie.id }) { movie ->
                MovieDetailsCard(
                    title = movie.title,
                    rating = movie.voteAverage,
                    imageUrl = movie.posterPath,
                    genres = movie.genres,
                    modifier = Modifier.animateItem(),
                    isBookmarked = true,
                    onBookmark = { onBookmark(movie) },
                    overview = movie.overview,
                    onClick = { onGoToDetail(movie) },
                )
            }
        }
    }
}

@Composable
private fun BookmarkError() {
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

@Preview
@Composable
fun PreviewBookmarkLoading() {
    MovieShowcaseTheme {
        Surface {
            val uiState = BookmarkState.Loading
            BookmarkContent(
                uiState = uiState,
                onBookmark = {},
                onGoToDetail = {},
                paddingValues = PaddingValues(0.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewBookmarkError() {
    MovieShowcaseTheme {
        Surface {
            val uiState = BookmarkState.Error("error")
            BookmarkContent(
                uiState = uiState,
                onBookmark = {},
                onGoToDetail = {},
                paddingValues = PaddingValues(0.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewBookmarkSuccess() {
    MovieShowcaseTheme {
        Surface {
            val uiState = BookmarkState.Success(
                bookmarkedMovies = movie(5)
            )
            BookmarkContent(
                uiState = uiState,
                onBookmark = {},
                onGoToDetail = {},
                paddingValues = PaddingValues(0.dp)
            )
        }
    }
}