package com.uibuilder.app.data.repository

import com.uibuilder.app.data.db.AppDatabase
import com.uibuilder.app.data.db.entity.AnimationEntity
import com.uibuilder.app.data.db.entity.ComponentEntity
import com.uibuilder.app.data.db.entity.ExportHistoryEntity
import com.uibuilder.app.data.db.entity.ProjectEntity
import com.uibuilder.app.data.db.entity.VersionHistoryEntity
import com.uibuilder.app.domain.model.AnimationConfig
import com.uibuilder.app.domain.model.AnimationInterpolator
import com.uibuilder.app.domain.model.AnimationType
import com.uibuilder.app.domain.model.ComponentProperties
import com.uibuilder.app.domain.model.ComponentType
import com.uibuilder.app.domain.model.Project
import com.uibuilder.app.domain.model.RepeatMode
import com.uibuilder.app.domain.model.SequenceMode
import com.uibuilder.app.domain.model.UiComponent
import com.uibuilder.app.util.JsonUtils
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    private val database: AppDatabase,
    private val moshi: Moshi
) {

    private val projectDao get() = database.projectDao()
    private val componentDao get() = database.componentDao()
    private val animationDao get() = database.animationDao()
    private val historyDao get() = database.historyDao()

    fun observeProjects(): Flow<List<Project>> =
        projectDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    fun observeProject(projectId: String): Flow<Project?> =
        projectDao.observeById(projectId).map { it?.toDomain() }

    suspend fun getProject(projectId: String): Project? {
        val projectEntity = projectDao.getById(projectId) ?: return null
        val components = componentDao.getByProject(projectId).map { it.toDomain() }
        return projectEntity.toDomain().copy(components = components)
    }

    suspend fun createProject(name: String, description: String = ""): Project {
        val project = Project(name = name, description = description)
        projectDao.upsert(project.toEntity())
        return project
    }

    suspend fun saveProject(project: Project) {
        projectDao.upsert(project.copy(lastModified = System.currentTimeMillis()).toEntity())
        componentDao.deleteByProject(project.id)
        componentDao.upsertAll(project.components.map { it.toEntity(project.id) })

        project.components.forEach { component ->
            animationDao.deleteByComponent(component.id)
            animationDao.upsertAll(component.animations.map { it.toEntity(component.id) })
        }
    }

    suspend fun deleteProject(projectId: String) {
        projectDao.deleteById(projectId)
    }

    suspend fun renameProject(projectId: String, newName: String) {
        val project = projectDao.getById(projectId) ?: return
        projectDao.update(
            project.copy(
                name = newName,
                lastModified = System.currentTimeMillis()
            )
        )
    }

    suspend fun addComponent(projectId: String, component: UiComponent) {
        val maxZ = componentDao.getMaxZIndex(projectId) ?: -1
        val withZ = component.copy(zIndex = maxZ + 1, updatedAt = System.currentTimeMillis())
        componentDao.upsert(withZ.toEntity(projectId))
    }

    suspend fun updateComponent(projectId: String, component: UiComponent) {
        componentDao.upsert(component.copy(updatedAt = System.currentTimeMillis()).toEntity(projectId))
    }

    suspend fun deleteComponent(componentId: String) {
        componentDao.deleteById(componentId)
    }

    suspend fun reorderComponent(componentId: String, newZIndex: Int) {
        componentDao.updateZIndex(componentId, newZIndex, System.currentTimeMillis())
    }

    suspend fun groupComponents(parentId: String, childIds: List<String>) {
        val now = System.currentTimeMillis()
        childIds.forEach { childId ->
            componentDao.updateParent(childId, parentId, now)
        }
    }

    suspend fun ungroupComponents(childIds: List<String>) {
        val now = System.currentTimeMillis()
        childIds.forEach { childId ->
            componentDao.updateParent(childId, null, now)
        }
    }

    suspend fun createVersionSnapshot(projectId: String, message: String, branch: String = "main") {
        val project = getProject(projectId) ?: return
        val snapshotJson = JsonUtils.toJson(moshi, project, Project::class.java)
        val parentId = historyDao.getLatestVersionOnBranch(projectId, branch)?.id
        historyDao.insertVersion(
            VersionHistoryEntity(
                id = UUID.randomUUID().toString(),
                projectId = projectId,
                message = message,
                snapshotJson = snapshotJson,
                branchName = branch,
                parentId = parentId
            )
        )
    }

    fun observeVersionHistory(projectId: String): Flow<List<VersionHistoryEntity>> =
        historyDao.observeVersionHistory(projectId)

    suspend fun getBranches(projectId: String): List<String> = historyDao.getBranches(projectId)

    suspend fun restoreVersion(versionId: String) {
        val version = historyDao.getVersionById(versionId) ?: return
        val project = JsonUtils.fromJson(moshi, version.snapshotJson, Project::class.java) ?: return
        saveProject(project)
    }

    fun observeExportHistory(): Flow<List<ExportHistoryEntity>> =
        historyDao.observeExportHistory()

    suspend fun recordExport(
        projectId: String,
        projectName: String,
        format: String,
        filePath: String,
        fileSizeBytes: Long,
        success: Boolean = true
    ) {
        historyDao.insertExport(
            ExportHistoryEntity(
                id = UUID.randomUUID().toString(),
                projectId = projectId,
                projectName = projectName,
                exportFormat = format,
                filePath = filePath,
                fileSizeBytes = fileSizeBytes,
                success = success
            )
        )
    }

    suspend fun getExportCount(projectId: String): Int =
        historyDao.getExportCount(projectId)

    private fun ProjectEntity.toDomain(): Project = Project(
        id = id,
        name = name,
        description = description,
        createdAt = createdAt,
        lastModified = lastModified,
        themeId = themeId,
        typographyId = typographyId,
        rtlMode = rtlMode
    )

    private fun Project.toEntity(): ProjectEntity = ProjectEntity(
        id = id,
        name = name,
        description = description,
        createdAt = createdAt,
        lastModified = lastModified,
        themeId = themeId,
        typographyId = typographyId,
        rtlMode = rtlMode
    )

    private fun ComponentEntity.toDomain(): UiComponent {
        val props = JsonUtils.fromJson(moshi, propertiesJson, ComponentProperties::class.java)
            ?: ComponentProperties()
        return UiComponent(
            id = id,
            projectId = projectId,
            type = ComponentType.valueOf(type),
            properties = props,
            x = x,
            y = y,
            width = width,
            height = height,
            zIndex = zIndex,
            parentId = parentId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun UiComponent.toEntity(projectId: String): ComponentEntity {
        val json = JsonUtils.toJson(moshi, properties, ComponentProperties::class.java)
        return ComponentEntity(
            id = id,
            projectId = projectId,
            type = type.name,
            propertiesJson = json,
            x = x,
            y = y,
            width = width,
            height = height,
            zIndex = zIndex,
            parentId = parentId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun AnimationEntity.toDomain(): AnimationConfig = AnimationConfig(
        id = id,
        type = AnimationType.valueOf(type),
        durationMs = durationMs,
        delayMs = delayMs,
        interpolator = AnimationInterpolator.valueOf(interpolator),
        repeatCount = repeatCount,
        repeatMode = RepeatMode.valueOf(repeatMode),
        sequenceOrder = sequenceOrder,
        sequenceMode = SequenceMode.valueOf(sequenceMode)
    )

    private fun AnimationConfig.toEntity(componentId: String): AnimationEntity = AnimationEntity(
        id = id,
        componentId = componentId,
        type = type.name,
        durationMs = durationMs,
        delayMs = delayMs,
        interpolator = interpolator.name,
        repeatCount = repeatCount,
        repeatMode = repeatMode.name,
        sequenceOrder = sequenceOrder,
        sequenceMode = sequenceMode.name
    )
}
