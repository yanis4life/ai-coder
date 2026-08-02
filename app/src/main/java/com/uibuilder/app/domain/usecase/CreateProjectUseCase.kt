package com.uibuilder.app.domain.usecase

import com.uibuilder.app.data.repository.ProjectRepository
import com.uibuilder.app.domain.model.Project
import javax.inject.Inject

class CreateProjectUseCase @Inject constructor(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(name: String, description: String = ""): Project {
        require(name.isNotBlank()) { "Project name cannot be blank" }
        return repository.createProject(name, description)
    }
}
