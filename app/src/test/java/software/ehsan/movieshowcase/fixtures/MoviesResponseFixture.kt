package software.ehsan.movieshowcase.fixtures

import software.ehsan.movieshowcase.core.network.model.MovieResponse
import software.ehsan.movieshowcase.core.network.model.MoviesResponse

object MoviesResponseFixture {
    val emptyMovieResponse = MoviesResponse(1, emptyList(),1,0)
    val fiveMoviesResponse = MoviesResponse(1, moviesResponse(5),1,5)
    fun moviesResponse(size:Int, hasGenre:Boolean = false) = List(size){ i->
        MovieResponse(id = i, title = "title $i", overview = "overview $i", posterPath = "poster path $i", genres = if(hasGenre) listOf(i) else null, voteAverage = i.mod(5).toFloat())
    }
}