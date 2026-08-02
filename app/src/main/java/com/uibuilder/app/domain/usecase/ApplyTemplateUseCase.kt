package com.uibuilder.app.domain.usecase

import com.uibuilder.app.data.repository.ProjectRepository
import com.uibuilder.app.data.repository.TemplateProvider
import com.uibuilder.app.domain.model.UiComponent
import javax.inject.Inject

class ApplyTemplateUseCase @Inject constructor(
    private val repository: ProjectRepository,
    private val templateProvider: TemplateProvider
) {
    suspend operator fun invoke(templateId: String, projectId: String): List<UiComponent> {
        val components = templateProvider.buildComponents(templateId, projectId)

        val project = repository.getProject(projectId) ?: return emptyList()
        repository.saveProject(project.copy(components = components))
        return components
    }
}
