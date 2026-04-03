package edu.nd.pmcburne.hello

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MainUIState(
    val selectedTag: String = "core"
)

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUIState())
    val uiState: StateFlow<MainUIState> = _uiState.asStateFlow()

    fun selectTag(tag: String) {
        _uiState.update { currentState ->
            currentState.copy(selectedTag = tag)
        }
    }
}