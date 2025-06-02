package software.ehsan.movieshowcase.core.database

sealed class DatabaseException(message: String? = null) : Exception(message) {
    data class GenericDatabaseException(val error: String? = null) :
        DatabaseException(message = error)
}