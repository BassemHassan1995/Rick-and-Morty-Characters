package bassem.task.characters.data.remote.utils

import bassem.task.characters.data.remote.exception.ApiException
import bassem.task.characters.data.remote.exception.ClientException
import bassem.task.characters.data.remote.exception.NetworkException
import bassem.task.characters.data.remote.exception.NotFoundException
import bassem.task.characters.data.remote.exception.ServerException
import bassem.task.characters.data.remote.exception.TimeoutException
import bassem.task.characters.data.remote.exception.UnknownException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Exception.toApiException(): ApiException {
    return when (this) {
        is HttpException -> handleHttpException()
        is SocketTimeoutException -> TimeoutException(
            message = "Request timed out. Please try again.",
            cause = this
        )

        is UnknownHostException -> NetworkException(
            message = "No internet connection. Please check your network.",
            cause = this
        )

        is IOException -> NetworkException(
            message = "Network error: ${localizedMessage ?: "Check your connection"}",
            cause = this
        )

        else -> UnknownException(
            message = localizedMessage ?: "Unknown error occurred",
            cause = this
        )
    }
}

private fun HttpException.handleHttpException(): ApiException {
    val readableMessage = try {
        val errorBody = response()?.errorBody()?.string()
        if (!errorBody.isNullOrBlank()) {
            val jsonObject = JSONObject(errorBody)
            jsonObject.optString("error").takeIf { it.isNotEmpty() }
                ?: localizedMessage
        } else {
            localizedMessage
        }
    } catch (_: Exception) {
        localizedMessage
    }

    return when (code()) {
        404 -> NotFoundException(readableMessage, this)
        408 -> TimeoutException(readableMessage, this)
        in 400..499 -> ClientException(code(), readableMessage, this)
        in 500..599 -> ServerException(code(), readableMessage, this)
        else -> NetworkException(readableMessage, this)
    }
}
