package com.uibuilder.app.domain.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
@JsonClass(generateAdapter = true)
data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis(),
    val themeId: String = "default",
    val typographyId: String = "default",
    val rtlMode: Boolean = false,
    val components: List<UiComponent> = emptyList()
) : Parcelable {
    fun touch(): Project = copy(lastModified = System.currentTimeMillis())
}
