package bassem.task.characters.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseViewModel<Event : ViewEvent, State : ViewState, Effect : ViewEffect>(
    initialState: State
) : ViewModel() {

    // UI State
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    // One-time effects
    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    protected fun setState(reducer: State.() -> State) {
        _state.update { it.reducer() }
    }

    protected fun sendEffect(builder: () -> Effect) {
        viewModelScope.launch {
            _effect.send(builder())
        }
    }

    abstract fun onEvent(event: Event)
}
