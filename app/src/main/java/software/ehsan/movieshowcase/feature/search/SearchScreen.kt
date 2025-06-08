package software.ehsan.movieshowcase.feature.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import software.ehsan.movieshowcase.R
import software.ehsan.movieshowcase.core.designsystem.component.AppTopAppBar
import software.ehsan.movieshowcase.core.designsystem.component.CenteredLoading
import software.ehsan.movieshowcase.core.designsystem.component.MovieDetailsCard
import software.ehsan.movieshowcase.core.designsystem.component.SearchBar
import software.ehsan.movieshowcase.core.designsystem.theme.spacing
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.core.model.Movies
import software.ehsan.movieshowcase.feature.search.SearchViewModel.SearchIntent
import software.ehsan.movieshowcase.feature.search.SearchViewModel.SearchUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onGoToDetails: (movieId: Movie) -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery = viewModel.searchQuery.collectAsStateWithLifecycle()

    Scaffold(topBar = {
        AppTopAppBar(
            title = buildAnnotatedString {
                append(stringResource(R.string.searchScreen_appBarTitle))
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.tertiary)) {
                    append(stringResource(R.string.all_dot))
                }
            },
            color = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )
    }
    ) { paddingValues ->
        SearchContent(
            uiState.value,
            searchQuery.value,
            paddingValues = paddingValues,
            onGoToDetails = onGoToDetails,
            onBookmark = {
                viewModel.handleIntent(
                    SearchIntent.BookmarkMovie(it)
                )
            },
            onQueryChange = {
                viewModel.handleIntent(
                    SearchIntent.UpdateSearchQuery(it)
                )
            },
            onSearch = {
                viewModel.handleIntent(
                    SearchIntent.Search
                )
            }
        )
    }
}

@Composable
fun SearchContent(
    searchUiState: SearchUiState,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    paddingValues: PaddingValues,
    onGoToDetails: (Movie) -> Unit,
    onBookmark: (Movie) -> Unit,
    onSearch: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = MaterialTheme.spacing.l)
    ) {
        SearchBar(
            searchQuery,
            onQueryChange = { onQueryChange(it) },
            onSearch = {
                onSearch(it)
            },
            onClear = { onQueryChange("") },
            placeholder = stringResource(R.string.searchScreen_searchbarPlaceholder)
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))
        when (searchUiState) {
            is SearchUiState.Idle -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.searchScreen_idleSearch))
                }
            }

            is SearchUiState.Success -> {
                SearchSuccess(searchUiState.movies, onGoToDetails, onBookmark)
            }

            is SearchUiState.Loading -> {
                CenteredLoading()
            }

            is SearchUiState.Error -> {
                SearchError()
            }
        }
    }
}

@Composable
fun SearchSuccess(
    movies: Movies,
    onGoToDetails: (Movie) -> Unit,
    onBookmark: (Movie) -> Unit
) {
    if (movies.totalResults == 0) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.searchScreen_noResults))
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.l)
        ) {
            item {
                Text("Search results ${movies.totalResults}")
            }
            items(movies.results, key = { movie -> movie.id }) { movie ->
                MovieDetailsCard(
                    title = movie.title,
                    rating = movie.voteAverage,
                    imageUrl = movie.posterPath,
                    genres = movie.genres,
                    modifier = Modifier.animateItem(),
                    isBookmarked = movie.isBookmarked,
                    onBookmark = { onBookmark(movie) },
                    overview = movie.overview,
                    onClick = { onGoToDetails(movie) },
                )
            }
        }
    }

}

@Composable
private fun SearchError() {
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
