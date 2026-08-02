package com.uibuilder.app.domain.usecase

import com.uibuilder.app.data.repository.ProjectRepository
import com.uibuilder.app.domain.model.UiComponent
import javax.inject.Inject

class AddComponentUseCase @Inject constructor(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(projectId: String, component: UiComponent) {
        repository.addComponent(projectId, component)
    }
}
