package com.uibuilder.app.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uibuilder.app.data.repository.ProjectRepository
import com.uibuilder.app.domain.model.Project
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ProjectRepository
) : ViewModel() {

    val projects: StateFlow<List<Project>> = repository.observeProjects()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _currentProjectId = MutableStateFlow<String?>(null)
    val currentProjectId: StateFlow<String?> = _currentProjectId.asStateFlow()

    fun createAndOpenProject(name: String, description: String = "") {
        viewModelScope.launch {
            val project = repository.createProject(name, description)
            _currentProjectId.value = project.id
        }
    }

    fun openProject(projectId: String) {
        _currentProjectId.value = projectId
    }

    fun closeProject() {
        _currentProjectId.value = null
    }
}
