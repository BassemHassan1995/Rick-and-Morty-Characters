package bassem.task.characters.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base ViewModel for UDF screens.
 *
 * - [state] holds long-lived immutable UI state.
 * - [effect] emits one-off events such as navigation or snackbars.
 *
 * Effects use [SharedFlow] and wait for an active collector before emission to
 * reduce the chance of losing events during initial composition.
 */
abstract class BaseViewModel<Event : ViewEvent, State : ViewState, Effect : ViewEffect>(
    initialState: State
) : ViewModel() {

    // UI State
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    // One-time effects
    private val _effect = MutableSharedFlow<Effect>(extraBufferCapacity = 1)
    val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    protected fun setState(reducer: State.() -> State) {
        _state.update { it.reducer() }
    }

    protected fun sendEffect(builder: () -> Effect) {
        viewModelScope.launch {
            _effect.subscriptionCount.first { count -> count > 0 }
            _effect.emit(builder())
        }
    }

    abstract fun onEvent(event: Event)
}
