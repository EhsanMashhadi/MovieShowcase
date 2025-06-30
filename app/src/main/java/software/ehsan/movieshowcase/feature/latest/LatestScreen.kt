package software.ehsan.movieshowcase.feature.latest

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import software.ehsan.movieshowcase.R
import software.ehsan.movieshowcase.core.designsystem.component.AppTopAppBar
import software.ehsan.movieshowcase.core.designsystem.component.CenteredLoading
import software.ehsan.movieshowcase.core.designsystem.component.GenreTag
import software.ehsan.movieshowcase.core.designsystem.component.InlineError
import software.ehsan.movieshowcase.core.designsystem.component.MovieCard
import software.ehsan.movieshowcase.core.designsystem.component.MovieShowcaseIcons
import software.ehsan.movieshowcase.core.designsystem.theme.spacing
import software.ehsan.movieshowcase.core.model.Genre
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.feature.latest.LatestViewModel.LatestIntent
import software.ehsan.movieshowcase.feature.latest.LatestViewModel.UiState
import software.ehsan.movieshowcase.feature.search.SearchViewModel

@Composable
fun LatestScreen(
    viewModel: LatestViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onGoToDetails: (movieId: Movie) -> Unit
) {
    val screenState = viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is SearchViewModel.SearchEvent.ShowToast -> {
                    Toast.makeText(
                        context,
                        context.getString(event.messageResId),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = { TopBar(onBack) }
    ) { paddingValues ->
        LatestContent(
            screenState.value,
            paddingValues,
            onGoToDetails,
            onFilterByGenre = {
                viewModel.handleIntent(LatestIntent.LoadLatest(selectedGenre = it))
            },
            onBookmark = { viewModel.handleIntent(LatestIntent.BookmarkMovie(it)) })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onBack: () -> Unit) {
    AppTopAppBar(
        title = buildAnnotatedString {
            append(stringResource(R.string.latestScreen_appBarTitle))
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.tertiary)) {
                append(stringResource(R.string.all_dot))
            }
        },
        color = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        navigationIcon = {
            IconButton(
                onClick = {
                    onBack()
                },
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(MovieShowcaseIcons.backIcon),
                    tint = MaterialTheme.colorScheme.tertiary,
                    contentDescription = "Back to previous screen"
                )
            }
        })
}

@Composable
fun LatestContent(
    latestScreenUiState: LatestViewModel.LatestScreenUiState,
    paddingValues: PaddingValues,
    onGoToDetails: (Movie) -> Unit,
    onFilterByGenre: (Genre) -> Unit,
    onBookmark: (Movie) -> Unit
) {
    val selectedGenre = latestScreenUiState.selectedGenre
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = MaterialTheme.spacing.l)
    ) {
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))
        val moviesState = latestScreenUiState.movies
        val genresState = latestScreenUiState.genres
        var movies: LazyPagingItems<Movie>? = null
        val totalResultCount = if (moviesState is UiState.Success) {
            movies = moviesState.data.movies.collectAsLazyPagingItems()
            if (movies.loadState.refresh is LoadState.NotLoading) latestScreenUiState.totalResultCount else null
        } else 0

        GenreSection(
            genresState,
            totalResultCount,
            selectedGenre = selectedGenre,
            onFilterByGenre = onFilterByGenre
        )
        MoviesSection(
            movies,
            moviesState = moviesState,
            onGoToDetails = onGoToDetails,
            onBookmark = onBookmark
        )

    }
}


@Composable
fun GenreSection(
    state: UiState<List<Genre>>,
    totalMoviesCount: Int?,
    selectedGenre: Genre?,
    onFilterByGenre: (Genre) -> Unit
) {
    when (state) {
        is UiState.Idle -> {}
        is UiState.Error -> {}
        is UiState.Loading -> CenteredLoading()
        is UiState.Success -> {
            if (state.data.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
                ) {
                    items(state.data, key = { it.id }) { genre ->
                        GenreTag(
                            genre = genre,
                            isActive = selectedGenre == genre,
                            number = if (selectedGenre == genre) totalMoviesCount else null
                        ) {
                            onFilterByGenre(it)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxl))
            }
        }
    }
}

@Composable
fun MoviesSection(
    movies: LazyPagingItems<Movie>?,
    moviesState: UiState<LatestViewModel.LatestMoviesUiState>,
    onGoToDetails: (Movie) -> Unit,
    onBookmark: (Movie) -> Unit
) {
    when (moviesState) {
        is UiState.Idle -> {}
        is UiState.Error -> {
            LatestScreenErrorContent()
        }

        is UiState.Loading -> {
            CenteredLoading()
        }

        is UiState.Success -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                movies?.let {
                    MoviesSuccess(it, onBookmark, onGoToDetails)
                }
            }
        }
    }
}

@Composable
private fun MoviesSuccess(
    movies: LazyPagingItems<Movie>,
    onBookmark: (Movie) -> Unit,
    onGoToDetails: (Movie) -> Unit
) {
    movies.apply {
        when {
            loadState.refresh is LoadState.Loading -> {
                CenteredLoading()
            }

            loadState.refresh is LoadState.Error -> {
                Text(stringResource(R.string.all_error))
            }

            loadState.refresh is LoadState.NotLoading && movies.itemCount == 0 -> {
                Text(stringResource(R.string.searchScreen_noResults))
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.l),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xl)
                ) {
                    items(
                        count = movies.itemCount,
                        key = movies.itemKey { it.id }
                    ) { index ->
                        val movie = movies[index]
                        movie?.let {
                            MovieCard(
                                title = movie.title,
                                rating = movie.voteAverage,
                                imageUrl = movie.posterPath,
                                genres = null,
                                isBookmarked = movie.isBookmarked,
                                portrait = true,
                                onBookmark = { onBookmark(it) },
                                onClick = { onGoToDetails(movie) },
                            )
                        }
                    }
                    Log.d("Loading state", loadState.toString())
                    when {
                        loadState.append is LoadState.Loading -> {
                            item(span = { GridItemSpan(maxLineSpan) }) { CenteredLoading(modifier = Modifier) }
                        }

                        loadState.append is LoadState.Error -> {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                InlineError(
                                    error = stringResource(R.string.all_error),
                                    onRetry = { retry() })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LatestScreenErrorContent() {
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