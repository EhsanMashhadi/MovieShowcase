package software.ehsan.movieshowcase.core.network.service

sealed class ApiException(message: String? = null) : Exception(message) {
    companion object {
        const val UNKNOWN_ERROR = "Unknown Error"
    }

    data class BadRequestException(val error: String? = null) : ApiException(message = error)
    data class UnauthorizedException(val error: String? = null) : ApiException(message = error)
    data class ServerException(val code: Int, val error: String? = null) :
        ApiException(message = error)

    data class EmptyBodyException(val error: String? = null) : ApiException(message = error)
    data class InternetException(val error: String? = null) : ApiException(message = error)
    data class UnknownException(val error: String? = null) : ApiException(message = error)
}