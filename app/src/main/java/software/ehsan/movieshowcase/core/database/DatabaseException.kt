package software.ehsan.movieshowcase.core.database

sealed class DatabaseException(message: String? = null) : Exception(message) {
    data class GenericApiException(val error: String? = null) : DatabaseException(message = error)
}