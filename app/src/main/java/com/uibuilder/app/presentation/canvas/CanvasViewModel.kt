package com.uibuilder.app.presentation.canvas

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uibuilder.app.data.repository.ProjectRepository
import com.uibuilder.app.domain.model.ComponentType
import com.uibuilder.app.domain.model.UiComponent
import com.uibuilder.app.util.Memento
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CanvasUiState(
    val projectId: String? = null,
    val projectName: String = "",
    val components: List<UiComponent> = emptyList(),
    val selectedIds: Set<String> = emptySet(),
    val clipboard: List<UiComponent> = emptyList(),
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val showGrid: Boolean = true,
    val snapToGrid: Boolean = false,
    val rtlMode: Boolean = false
)

sealed interface CanvasEvent {
    data class ShowMessage(val message: String) : CanvasEvent
    data class ShowDeleteConfirmation(val componentId: String) : CanvasEvent
    data class OpenProperties(val componentId: String) : CanvasEvent
}

@HiltViewModel
class CanvasViewModel @Inject constructor(
    private val repository: ProjectRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CanvasUiState())
    val uiState: StateFlow<CanvasUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CanvasEvent>()
    val events: SharedFlow<CanvasEvent> = _events.asSharedFlow()

    private val history = Memento<List<UiComponent>>(maxHistory = 100)

    fun loadProject(projectId: String) {
        viewModelScope.launch {
            val project = repository.getProject(projectId) ?: return@launch
            _uiState.update {
                it.copy(
                    projectId = project.id,
                    projectName = project.name,
                    components = project.components,
                    rtlMode = project.rtlMode
                )
            }
            history.clear()
            history.save(project.components)
            updateUndoRedoFlags()
        }
    }

    fun addComponent(type: ComponentType, x: Float, y: Float) {
        val state = _uiState.value
        val projectId = state.projectId ?: return
        val newComponent = UiComponent(
            id = UUID.randomUUID().toString(),
            projectId = projectId,
            type = type,
            x = x,
            y = y,
            zIndex = (state.components.maxOfOrNull { it.zIndex } ?: -1) + 1
        )
        val newComponents = state.components + newComponent
        updateComponents(newComponents, persistComponent = newComponent)
        selectComponent(newComponent.id)
    }

    fun updateComponent(updated: UiComponent) {
        val state = _uiState.value
        val newComponents = state.components.map { if (it.id == updated.id) updated else it }
        updateComponents(newComponents, persistComponent = updated)
    }

    fun updateComponentGeometry(
        componentId: String,
        x: Float,
        y: Float,
        width: Float,
        height: Float
    ) {
        val state = _uiState.value
        val newComponents = state.components.map { c ->
            if (c.id == componentId) c.copy(x = x, y = y, width = width, height = height) else c
        }
        _uiState.update { it.copy(components = newComponents) }

        viewModelScope.launch {
            repository.updateComponent(state.projectId ?: return@launch, newComponents.first { it.id == componentId })
        }
    }

    fun commitGeometryChange() {
        history.save(_uiState.value.components)
        updateUndoRedoFlags()
    }

    fun selectComponent(componentId: String) {
        _uiState.update { it.copy(selectedIds = setOf(componentId)) }
    }

    fun toggleSelection(componentId: String) {
        _uiState.update { state ->
            val newSelection = state.selectedIds.toMutableSet().apply {
                if (contains(componentId)) remove(componentId) else add(componentId)
            }
            state.copy(selectedIds = newSelection)
        }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedIds = emptySet()) }
    }

    fun deleteSelected() {
        val state = _uiState.value
        if (state.selectedIds.isEmpty()) {
            viewModelScope.launch { _events.emit(CanvasEvent.ShowMessage("No component selected")) }
            return
        }

        if (state.selectedIds.size == 1) {
            viewModelScope.launch {
                _events.emit(CanvasEvent.ShowDeleteConfirmation(state.selectedIds.first()))
            }
        } else {
            performDelete(state.selectedIds)
        }
    }

    fun performDelete(ids: Set<String>) {
        val state = _uiState.value
        val newComponents = state.components.filter { it.id !in ids }
        updateComponents(newComponents)
        _uiState.update { it.copy(selectedIds = emptySet()) }
        viewModelScope.launch {
            ids.forEach { repository.deleteComponent(it) }
        }
    }

    fun duplicateSelected() {
        val state = _uiState.value
        if (state.selectedIds.isEmpty()) return
        val toDuplicate = state.components.filter { it.id in state.selectedIds }
        val duplicates = toDuplicate.map { it.deepCopy().copy(x = it.x + 16, y = it.y + 16) }
        val newComponents = state.components + duplicates
        updateComponents(newComponents)
        _uiState.update { it.copy(selectedIds = duplicates.map { d -> d.id }.toSet()) }
        viewModelScope.launch {
            duplicates.forEach { repository.addComponent(state.projectId ?: return@launch, it) }
        }
    }

    fun cutSelected() {
        val state = _uiState.value
        if (state.selectedIds.isEmpty()) return
        val toCut = state.components.filter { it.id in state.selectedIds }
        _uiState.update { it.copy(clipboard = toCut) }
        performDelete(state.selectedIds)
    }

    fun copySelected() {
        val state = _uiState.value
        if (state.selectedIds.isEmpty()) return
        val toCopy = state.components.filter { it.id in state.selectedIds }
        _uiState.update { it.copy(clipboard = toCopy) }
    }

    fun paste() {
        val state = _uiState.value
        val projectId = state.projectId ?: return
        if (state.clipboard.isEmpty()) return
        val pasted = state.clipboard.map { it.deepCopy().copy(projectId = projectId, x = it.x + 32, y = it.y + 32) }
        val newComponents = state.components + pasted
        updateComponents(newComponents)
        _uiState.update { it.copy(selectedIds = pasted.map { it.id }.toSet()) }
        viewModelScope.launch {
            pasted.forEach { repository.addComponent(projectId, it) }
        }
    }

    fun bringToFront() {
        val state = _uiState.value
        if (state.selectedIds.isEmpty()) return
        val maxZ = state.components.maxOfOrNull { it.zIndex } ?: 0
        val selected = state.components.first { it.id in state.selectedIds }
        val newZ = maxZ + 1
        val newComponents = state.components.map { if (it.id == selected.id) it.copy(zIndex = newZ) else it }
        updateComponents(newComponents)
        viewModelScope.launch { repository.reorderComponent(selected.id, newZ) }
    }

    fun sendToBack() {
        val state = _uiState.value
        if (state.selectedIds.isEmpty()) return
        val minZ = state.components.minOfOrNull { it.zIndex } ?: 0
        val selected = state.components.first { it.id in state.selectedIds }
        val newZ = minZ - 1
        val newComponents = state.components.map { if (it.id == selected.id) it.copy(zIndex = newZ) else it }
        updateComponents(newComponents)
        viewModelScope.launch { repository.reorderComponent(selected.id, newZ) }
    }

    fun bringForward() {
        val state = _uiState.value
        if (state.selectedIds.isEmpty()) return
        val selected = state.components.first { it.id in state.selectedIds }
        val next = state.components.filter { it.zIndex > selected.zIndex }.minByOrNull { it.zIndex }
            ?: return
        swapZIndices(selected, next)
    }

    fun sendBackward() {
        val state = _uiState.value
        if (state.selectedIds.isEmpty()) return
        val selected = state.components.first { it.id in state.selectedIds }
        val prev = state.components.filter { it.zIndex < selected.zIndex }.maxByOrNull { it.zIndex }
            ?: return
        swapZIndices(selected, prev)
    }

    private fun swapZIndices(a: UiComponent, b: UiComponent) {
        val state = _uiState.value
        val newComponents = state.components.map {
            when (it.id) {
                a.id -> it.copy(zIndex = b.zIndex)
                b.id -> it.copy(zIndex = a.zIndex)
                else -> it
            }
        }
        updateComponents(newComponents)
        viewModelScope.launch {
            repository.reorderComponent(a.id, b.zIndex)
            repository.reorderComponent(b.id, a.zIndex)
        }
    }

    fun undo() {
        val previous = history.undo()
        if (previous == null) {
            viewModelScope.launch { _events.emit(CanvasEvent.ShowMessage("Nothing to undo")) }
            return
        }
        _uiState.update { it.copy(components = previous) }
        updateUndoRedoFlags()

        viewModelScope.launch {
            _uiState.value.projectId?.let { pid ->
                repository.saveProject(
                    com.uibuilder.app.domain.model.Project(
                        id = pid,
                        name = _uiState.value.projectName,
                        components = previous
                    )
                )
            }
        }
    }

    fun redo() {
        val next = history.redo()
        if (next == null) {
            viewModelScope.launch { _events.emit(CanvasEvent.ShowMessage("Nothing to redo")) }
            return
        }
        _uiState.update { it.copy(components = next) }
        updateUndoRedoFlags()
        viewModelScope.launch {
            _uiState.value.projectId?.let { pid ->
                repository.saveProject(
                    com.uibuilder.app.domain.model.Project(
                        id = pid,
                        name = _uiState.value.projectName,
                        components = next
                    )
                )
            }
        }
    }

    fun groupSelected(containerType: ComponentType = ComponentType.GRID) {
        val state = _uiState.value
        if (state.selectedIds.size < 2) return
        val projectId = state.projectId ?: return
        val groupContainer = UiComponent(
            id = UUID.randomUUID().toString(),
            projectId = projectId,
            type = containerType,
            zIndex = (state.components.maxOfOrNull { it.zIndex } ?: -1) + 1,
            children = state.selectedIds.toList()
        )
        val updatedChildren = state.components.map {
            if (it.id in state.selectedIds) it.copy(parentId = groupContainer.id) else it
        }
        updateComponents(updatedChildren + groupContainer)
        viewModelScope.launch {
            repository.addComponent(projectId, groupContainer)
            repository.groupComponents(groupContainer.id, state.selectedIds.toList())
        }
    }

    fun toggleGrid() {
        _uiState.update { it.copy(showGrid = !it.showGrid) }
    }

    fun toggleSnapToGrid() {
        _uiState.update { it.copy(snapToGrid = !it.snapToGrid) }
    }

    fun toggleRtl() {
        _uiState.update { it.copy(rtlMode = !it.rtlMode) }
    }

    private fun updateComponents(
        newComponents: List<UiComponent>,
        persistComponent: UiComponent? = null
    ) {
        _uiState.update { it.copy(components = newComponents) }
        history.save(newComponents)
        updateUndoRedoFlags()
        viewModelScope.launch {
            val projectId = _uiState.value.projectId ?: return@launch
            persistComponent?.let { repository.updateComponent(projectId, it) }
        }
    }

    private fun updateUndoRedoFlags() {
        _uiState.update { it.copy(canUndo = history.canUndo, canRedo = history.canRedo) }
    }
}
