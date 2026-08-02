package com.uibuilder.app.domain.usecase

import com.uibuilder.app.data.repository.ProjectRepository
import com.uibuilder.app.domain.model.UiComponent
import com.uibuilder.app.util.CssGenerator
import com.uibuilder.app.util.HtmlGenerator
import com.uibuilder.app.util.JavaScriptGenerator
import java.io.File
import javax.inject.Inject

class ExportProjectUseCase @Inject constructor(
    private val repository: ProjectRepository,
    private val htmlGenerator: HtmlGenerator,
    private val cssGenerator: CssGenerator,
    private val jsGenerator: JavaScriptGenerator
) {

    data class ExportResult(
        val htmlFile: File?,
        val cssFile: File?,
        val jsFile: File?,
        val totalBytes: Long
    )

    suspend operator fun invoke(
        projectId: String,
        exportDir: File,
        pageTitle: String,
        cssFileName: String,
        jsFileName: String,
        themeColor: String = "#6750A4"
    ): ExportResult {
        val project = repository.getProject(projectId)
            ?: error("Project not found: $projectId")

        val components = project.components
        val rootComponents = components.filter { it.parentId == null }

        val htmlContent = htmlGenerator.generate(pageTitle, cssFileName, jsFileName, rootComponents, components)
        val htmlFile = File(exportDir, "$pageTitle.html").apply {
            writeText(htmlContent)
        }

        val cssContent = cssGenerator.generate(components, themeColor)
        val cssFile = File(exportDir, "$cssFileName.css").apply {
            writeText(cssContent)
        }

        val jsContent = jsGenerator.generate(pageTitle, components)
        val jsFile = File(exportDir, "$jsFileName.js").apply {
            writeText(jsContent)
        }

        val totalBytes = htmlFile.length() + cssFile.length() + jsFile.length()

        repository.recordExport(
            projectId = projectId,
            projectName = project.name,
            format = "html+css+js",
            filePath = exportDir.absolutePath,
            fileSizeBytes = totalBytes,
            success = true
        )

        return ExportResult(htmlFile, cssFile, jsFile, totalBytes)
    }
}
