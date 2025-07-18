package software.ehsan.movieshowcase.feature.detail

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import software.ehsan.movieshowcase.R
import software.ehsan.movieshowcase.core.designsystem.component.AppTopAppBar
import software.ehsan.movieshowcase.core.designsystem.component.CenteredLoading
import software.ehsan.movieshowcase.core.designsystem.component.DisplayRating
import software.ehsan.movieshowcase.core.designsystem.component.ExpandableText
import software.ehsan.movieshowcase.core.designsystem.component.MovieShowcaseIcons
import software.ehsan.movieshowcase.core.designsystem.theme.MovieShowcaseTheme
import software.ehsan.movieshowcase.core.designsystem.theme.spacing
import software.ehsan.movieshowcase.core.model.Movie

@Composable
fun DetailScreen(
    movieId: Int,
    viewModel: DetailViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val showSaveButton = uiState.value is DetailState.Success
    val isBookmarked = (uiState.value as? DetailState.Success)?.movie?.isBookmarked == true
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is DetailEvent.ShowToast -> {
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
    ) { paddingValues ->
        Box {
            DetailContent(uiState.value, paddingValues)
            if (uiState.value is DetailState.Success) {
                DetailTopBar(
                    onBack = onBack,
                    showSaveButton = showSaveButton,
                    isBookmarked = isBookmarked,
                    onBookmark = { viewModel.handleIntent(DetailIntent.ToggleBookmark((uiState.value as DetailState.Success).movie)) }
                )
            }
        }

    }
    LaunchedEffect(Unit) {
        viewModel.handleIntent(DetailIntent.LoadDetail(movieId = movieId))
    }
}

@Composable
fun DetailContent(
    detailState: DetailState,
    paddingValues: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        when (detailState) {
            is DetailState.Idle -> {}
            is DetailState.Loading -> {
                CenteredLoading()
            }

            is DetailState.Success -> {
                DetailSuccessContent(movie = detailState.movie)
            }

            is DetailState.Error -> {
                DetailErrorContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailSuccessContent(movie: Movie) {
    val scrollableState = rememberScrollState()
    Column(modifier = Modifier.verticalScroll(scrollableState)) {
        if (movie.posterPath != null) {
            AsyncImage(
                model = movie.posterPath,
                contentDescription = stringResource(
                    R.string.contentDescription_moviePoster,
                    movie.title
                ),
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Fit
            )

        } else {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.spacing.topAppBarHeight)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = MaterialTheme.spacing.l)
        ) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))
            Text(movie.title, style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))
            DisplayRating(rating = movie.voteAverage)
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))
            if (!movie.genres.isNullOrEmpty()) {
                Text(movie.genres.joinToString(", "))
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))
            ExpandableText(
                text = movie.overview,
                expandingText = stringResource(R.string.all_seeMore),
                textStyle = SpanStyle(color = MaterialTheme.colorScheme.onBackground),
                expandingTextStyle = SpanStyle(
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxl))
        }
    }
}

@Composable
private fun DetailErrorContent() {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailTopBar(
    onBack: () -> Unit,
    showSaveButton: Boolean,
    isBookmarked: Boolean,
    onBookmark: (() -> Unit)
) {
    AppTopAppBar(
        color = TopAppBarDefaults.topAppBarColors()
            .copy(containerColor = Color.Transparent),
        actionItem = {
            if (showSaveButton) {
                val contentDescription =
                    if (isBookmarked) stringResource(R.string.contentDescription_unSave) else stringResource(
                        R.string.contentDescription_save
                    )
                IconButton(
                    onClick = { onBookmark() }, modifier = Modifier
                        .semantics {
                            onClick(contentDescription) {
                                onBookmark()
                                true
                            }
                        }
                        .clickable { onBookmark() }) {
                    val icon =
                        ImageVector.vectorResource(id = if (isBookmarked) MovieShowcaseIcons.SaveFilledIcon else MovieShowcaseIcons.SaveIcon)
                    Icon(
                        tint = MaterialTheme.colorScheme.tertiary,
                        imageVector = icon,
                        contentDescription = if (isBookmarked) stringResource(R.string.contentDescription_savedIcon) else stringResource(
                            R.string.contentDescription_saveIcon
                        )
                    )
                }
            }
        },
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
        }
    )
}

@Preview
@Composable
private fun PreviewDetailSuccess() {
    MovieShowcaseTheme {
        Surface {
            DetailSuccessContent(
                movie = Movie(
                    1,
                    title = "Movie Title",
                    overview = "Long movie overview ".repeat(30),
                    genres = listOf("drama", "romance"),
                    voteAverage = 3.5f,
                    posterPath = null,
                    isBookmarked = true
                )
            )
        }
    }
}

@Preview
@Composable
private fun PreviewDetailError() {
    MovieShowcaseTheme {
        Surface {
            DetailErrorContent()
        }
    }
}