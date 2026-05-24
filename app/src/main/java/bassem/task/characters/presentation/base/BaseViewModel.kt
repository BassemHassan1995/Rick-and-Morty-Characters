package bassem.task.characters.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseViewModel<Event : ViewEvent, State : ViewState, Effect : ViewEffect>(
    initialState: State
) : ViewModel() {

    // UI State
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    // One-time effects
    private val _effect = MutableSharedFlow<Effect>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect: Flow<Effect> = _effect.asSharedFlow()

    protected fun setState(reducer: State.() -> State) {
        _state.update { it.reducer() }
    }

    protected fun sendEffect(builder: () -> Effect) {
        viewModelScope.launch {
            _effect.emit(builder())
        }
    }

    abstract fun onEvent(event: Event)
}
