package software.ehsan.movieshowcase.fixtures

import retrofit2.Response
import software.ehsan.movieshowcase.core.network.model.GenreResponse
import software.ehsan.movieshowcase.core.network.model.GenresResponse

object GenreFixture {
    val genres = GenresResponse(genreResponse(10))
    fun genreResponse(count:Int) = List(count){ i->
        GenreResponse(i,"Genre $i")
    }
    fun getGenreMapping(genresResponse: Response<GenresResponse>): Map<Int, String> {
        return if(genresResponse.isSuccessful){
            genresResponse.body()?.let {
                it.genres.associateBy({ it.id }, { it.name })
            }?: emptyMap()
        } else emptyMap()
    }
}