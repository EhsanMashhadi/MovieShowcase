package software.ehsan.movieshowcase.core.model

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

data class PagedMovies(val movies: Flow<PagingData<Movie>>, val totalResultCount: Int)