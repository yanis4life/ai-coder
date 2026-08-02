package com.uibuilder.app.domain.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
@JsonClass(generateAdapter = true)
data class UiComponent(
    val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val type: ComponentType,
    val properties: ComponentProperties = ComponentProperties.forType(type),
    val x: Float = 0f,
    val y: Float = 0f,
    val width: Float = 0f,
    val height: Float = 0f,
    val zIndex: Int = 0,
    val parentId: String? = null,
    val children: List<String> = emptyList(),
    val animations: List<AnimationConfig> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable {

    fun deepCopy(newProjectId: String = projectId): UiComponent {
        return copy(
            id = UUID.randomUUID().toString(),
            projectId = newProjectId,
            properties = properties.copy(),
            animations = animations.map { it.copy(id = UUID.randomUUID().toString()) },
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
}
