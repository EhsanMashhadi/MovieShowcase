package software.ehsan.movieshowcase.feature.latest

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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import software.ehsan.movieshowcase.R
import software.ehsan.movieshowcase.core.designsystem.component.AppTopAppBar
import software.ehsan.movieshowcase.core.designsystem.component.CenteredLoading
import software.ehsan.movieshowcase.core.designsystem.component.GenreTag
import software.ehsan.movieshowcase.core.designsystem.component.MovieCard
import software.ehsan.movieshowcase.core.designsystem.component.MovieShowcaseIcons
import software.ehsan.movieshowcase.core.designsystem.theme.spacing
import software.ehsan.movieshowcase.core.model.Genre
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.core.model.Movies
import software.ehsan.movieshowcase.feature.latest.LatestViewModel.LatestIntent
import software.ehsan.movieshowcase.feature.latest.LatestViewModel.LatestScreenUiState
import software.ehsan.movieshowcase.feature.latest.LatestViewModel.UiState

@Composable
fun LatestScreen(
    viewModel: LatestViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onGoToDetails: (movieId: Movie) -> Unit,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = { TopBar(onBack) }
    ) { paddingValues ->
        LatestContent(uiState.value, paddingValues, onGoToDetails, onFilterByGenre = {
            viewModel.handleIntent(LatestIntent.LoadLatest(selectedGenre = it))
        })
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
    latestState: LatestScreenUiState,
    paddingValues: PaddingValues,
    onGoToDetails: (Movie) -> Unit,
    onFilterByGenre: (Genre) -> Unit
) {
    val selectedGenre = latestState.selectedGenre
    val genresState = latestState.genresState
    val moviesState = latestState.moviesState
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = MaterialTheme.spacing.l)
    ) {
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))
        val totalMoviesCount =
            if (moviesState is UiState.Success) moviesState.data.totalResultsCount else null
        GenreSection(
            genresState,
            totalMoviesCount,
            selectedGenre = selectedGenre,
            onFilterByGenre = onFilterByGenre
        )
        MoviesSection(
            moviesState = moviesState,
            onGoToDetails = onGoToDetails
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
        is UiState.Error -> {}
        is UiState.Loading -> CenteredLoading()
        is UiState.Success -> {
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

@Composable
fun MoviesSection(
    moviesState: UiState<Movies>,
    onGoToDetails: (Movie) -> Unit
) {
    when (moviesState) {
        is UiState.Error -> {
            LatestScreenErrorContent()
        }

        is UiState.Loading -> {
            CenteredLoading()
        }

        is UiState.Success<Movies> -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.l),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xl)
            ) {
                val movies = moviesState.data.results
                items(movies, key = { it.id }) { movie ->
                    MovieCard(
                        title = movie.title,
                        rating = movie.voteAverage,
                        imageUrl = movie.posterPath,
                        genres = null,
                        isBookmarked = movie.isBookmarked,
                        portrait = true,
                        onBookmark = {},
                        onClick = { onGoToDetails(movie) },
                    )
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