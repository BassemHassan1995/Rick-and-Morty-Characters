package bassem.task.characters.presentation.base

// Generic result wrapper for async operations
sealed class ResultState<out T> {
    object Loading : ResultState<Nothing>()
    data class Success<T>(val data: T) : ResultState<T>()
    data class Error(val message: String? = null) : ResultState<Nothing>()
}