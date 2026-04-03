package edu.nd.pmcburne.hello

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MainUIState(
    val selectedTag: String = "core",
    val allTags: List<String> = emptyList(),
    val allLocations: List<LocationEntity> = emptyList(),
    val filteredLocations: List<LocationEntity> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CampusRepository(application)

    private val _uiState = MutableStateFlow(MainUIState())
    val uiState: StateFlow<MainUIState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                repository.syncPlacemarkData()

                val locations = repository.getAllLocations()
                val tags = locations
                    .flatMap { it.tagList }
                    .distinct()
                    .sorted()

                val selected = if ("core" in tags) "core" else tags.firstOrNull().orEmpty()

                _uiState.value = _uiState.value.copy(
                    selectedTag = selected,
                    allTags = tags,
                    allLocations = locations,
                    filteredLocations = locations.filter { selected in it.tagList },
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load data."
                )
            }
        }
    }

    fun selectTag(tag: String) {
        val filtered = _uiState.value.allLocations.filter { tag in it.tagList }

        _uiState.value = _uiState.value.copy(
            selectedTag = tag,
            filteredLocations = filtered
        )
    }
}