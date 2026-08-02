package com.uibuilder.app.presentation.properties

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uibuilder.app.domain.model.ComponentProperties
import com.uibuilder.app.domain.model.UiComponent
import com.uibuilder.app.presentation.canvas.CanvasUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PropertiesUiState(
    val component: UiComponent? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class PropertiesViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(PropertiesUiState())
    val uiState: StateFlow<PropertiesUiState> = _uiState.asStateFlow()

    fun loadComponent(canvasState: CanvasUiState, componentId: String) {
        val component = canvasState.components.find { it.id == componentId } ?: return
        _uiState.update { it.copy(component = component) }
    }

    fun update(block: (ComponentProperties.ComponentPropertiesBuilder.() -> Unit)) {
        val current = _uiState.value.component ?: return
        val newProps = current.properties.copy(block)
        val updated = current.copy(properties = newProps, updatedAt = System.currentTimeMillis())
        _uiState.update { it.copy(component = updated) }
    }

    fun setProperties(props: ComponentProperties) {
        val current = _uiState.value.component ?: return
        val updated = current.copy(properties = props, updatedAt = System.currentTimeMillis())
        _uiState.update { it.copy(component = updated) }
    }
}
