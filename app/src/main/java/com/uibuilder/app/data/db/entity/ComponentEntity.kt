package com.uibuilder.app.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "components",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["project_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["project_id"]),
        Index(value = ["parent_id"])
    ]
)
data class ComponentEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "project_id")
    val projectId: String,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "properties_json")
    val propertiesJson: String,
    @ColumnInfo(name = "x")
    val x: Float = 0f,
    @ColumnInfo(name = "y")
    val y: Float = 0f,
    @ColumnInfo(name = "width")
    val width: Float = 0f,
    @ColumnInfo(name = "height")
    val height: Float = 0f,
    @ColumnInfo(name = "z_index")
    val zIndex: Int = 0,
    @ColumnInfo(name = "parent_id")
    val parentId: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
