package software.ehsan.movieshowcase.feature.search

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import software.ehsan.movieshowcase.core.designsystem.component.InlineError
import software.ehsan.movieshowcase.core.designsystem.component.MovieDetailsCard
import software.ehsan.movieshowcase.core.designsystem.component.SearchBar
import software.ehsan.movieshowcase.core.designsystem.theme.spacing
import software.ehsan.movieshowcase.core.model.Movie
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
        topBar = {
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
            onSearch = {
                viewModel.handleIntent(
                    SearchIntent.Search(it)
                )
            }
        )
    }
}

@Composable
fun SearchContent(
    searchUiState: SearchUiState,
    searchQuery: String,
    onSearch: (String) -> Unit,
    paddingValues: PaddingValues,
    onGoToDetails: (Movie) -> Unit,
    onBookmark: (Movie) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = MaterialTheme.spacing.l)
    ) {
        SearchBar(
            searchQuery,
            onQueryChange = { onSearch(it) },
            onClear = { onSearch("") },
            placeholder = stringResource(R.string.searchScreen_searchbarPlaceholder),
            onSearch = {}
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
                val movies = searchUiState.movies.collectAsLazyPagingItems()
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
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
                                SearchSuccess(
                                    totalResults = searchUiState.totalResult,
                                    movies = movies,
                                    onGoToDetails = onGoToDetails,
                                    onBookmark = onBookmark
                                )
                            }
                        }
                    }
                }
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
    totalResults: Int,
    movies: LazyPagingItems<Movie>,
    onGoToDetails: (Movie) -> Unit,
    onBookmark: (Movie) -> Unit
) {
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.l)
    ) {
        item {
            Text("${stringResource(R.string.searchScreen_totalSearchResultCount)} $totalResults")
        }
        items(
            count = movies.itemCount,
            key = movies.itemKey { it.id }
        ) { index ->
            val movie = movies[index]
            movie?.let {
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
        movies.apply {
            Log.d("Loading state", loadState.toString())
            when {
                loadState.append is LoadState.Loading -> {
                    item { CenteredLoading(modifier = Modifier) }
                }

                loadState.append is LoadState.Error -> {
                    item {
                        InlineError(
                            error = stringResource(R.string.all_error),
                            onRetry = { retry() })
                    }
                }
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
