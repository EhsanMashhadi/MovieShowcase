package software.ehsan.movieshowcase.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val voteAverage: Float,
    val posterPath: String?
)

