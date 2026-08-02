package com.uibuilder.app.presentation.templates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uibuilder.app.data.repository.TemplateProvider
import com.uibuilder.app.domain.usecase.ApplyTemplateUseCase
import com.uibuilder.app.domain.model.Template
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TemplatesViewModel @Inject constructor(
    private val templateProvider: TemplateProvider,
    private val applyTemplateUseCase: ApplyTemplateUseCase
) : ViewModel() {

    private val _templates = MutableStateFlow<List<Template>>(emptyList())
    val templates: StateFlow<List<Template>> = _templates.asStateFlow()

    fun loadTemplates() {
        _templates.value = templateProvider.allTemplates()
    }

    fun applyTemplate(templateId: String, projectId: String) {
        viewModelScope.launch {
            applyTemplateUseCase(templateId, projectId)
        }
    }
}
