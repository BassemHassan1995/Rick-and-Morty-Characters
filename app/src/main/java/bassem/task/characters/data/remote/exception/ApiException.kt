package bassem.task.characters.data.remote.exception

/**
 * Base class for all API-related exceptions
 */
sealed class ApiException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Exception thrown when a resource is not found (404)
 */
class NotFoundException(
    message: String,
    cause: Throwable? = null
) : ApiException(message, cause)

/**
 * Exception thrown when a request times out (408 or socket timeout)
 */
class TimeoutException(
    message: String,
    cause: Throwable? = null
) : ApiException(message, cause)

/**
 * Exception thrown for client errors (4xx)
 */
class ClientException(
    val code: Int,
    message: String,
    cause: Throwable? = null
) : ApiException(message, cause)

/**
 * Exception thrown for server errors (5xx)
 */
class ServerException(
    val code: Int,
    message: String,
    cause: Throwable? = null
) : ApiException(message, cause)

/**
 * Exception thrown for network-related issues
 */
class NetworkException(
    message: String = "Network error occurred",
    cause: Throwable? = null
) : ApiException(message, cause)

/**
 * Exception thrown for unknown/unexpected errors
 */
class UnknownException(
    message: String,
    cause: Throwable? = null
) : ApiException(message, cause)

