package com.uibuilder.app.presentation.export

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uibuilder.app.data.repository.ProjectRepository
import com.uibuilder.app.domain.usecase.ExportProjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exportUseCase: ExportProjectUseCase,
    private val repository: ProjectRepository
) : ViewModel() {

    private val _exportResult = MutableSharedFlow<ExportProjectUseCase.ExportResult?>()
    val exportResult: SharedFlow<ExportProjectUseCase.ExportResult?> = _exportResult

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error

    fun exportHtmlCssJs(
        projectId: String,
        pageTitle: String,
        cssFileName: String,
        jsFileName: String,
        themeColor: String
    ) {
        viewModelScope.launch {
            try {
                val exportDir = java.io.File(context.filesDir, "exports/$projectId").apply { mkdirs() }
                val result = exportUseCase(
                    projectId = projectId,
                    exportDir = exportDir,
                    pageTitle = pageTitle,
                    cssFileName = cssFileName,
                    jsFileName = jsFileName,
                    themeColor = themeColor
                )
                _exportResult.emit(result)
            } catch (e: Exception) {
                _error.emit("Export failed: ${e.message}")
            }
        }
    }

    fun exportFigmaJson(projectId: String) {
        viewModelScope.launch {
            try {
                val project = repository.getProject(projectId) ?: return@launch
                val figmaJson = buildString {
                    append("{\n")
                    append("  \"name\": \"${project.name}\",\n")
                    append("  \"document\": {\n")
                    append("    \"id\": \"0:0\",\n")
                    append("    \"name\": \"Document\",\n")
                    append("    \"children\": [\n")
                    project.components.forEachIndexed { idx, component ->
                        append("      {")
                        append("\"id\": \"${component.id}\", ")
                        append("\"name\": \"${component.type.displayName}\", ")
                        append("\"type\": \"FRAME\", ")
                        append("\"x\": ${component.x}, ")
                        append("\"y\": ${component.y}, ")
                        append("\"width\": ${component.width}, ")
                        append("\"height\": ${component.height}")
                        append("}")
                        if (idx < project.components.size - 1) append(",")
                        append("\n")
                    }
                    append("    ]\n")
                    append("  }\n")
                    append("}\n")
                }
                val exportDir = java.io.File(context.filesDir, "exports/$projectId").apply { mkdirs() }
                val file = java.io.File(exportDir, "figma_${projectId}.json").apply {
                    writeText(figmaJson)
                }
                _exportResult.emit(
                    ExportProjectUseCase.ExportResult(
                        htmlFile = file,
                        cssFile = null,
                        jsFile = null,
                        totalBytes = file.length()
                    )
                )
            } catch (e: Exception) {
                _error.emit("Figma export failed: ${e.message}")
            }
        }
    }
}
