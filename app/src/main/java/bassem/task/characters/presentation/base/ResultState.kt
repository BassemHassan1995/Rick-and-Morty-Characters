package bassem.task.characters.presentation.base

// Generic result wrapper for async operations
sealed class ResultState<out T> {
    object Idle : ResultState<Nothing>()
    object Loading : ResultState<Nothing>()
    data class Success<T>(val data: T) : ResultState<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : ResultState<Nothing>()
}