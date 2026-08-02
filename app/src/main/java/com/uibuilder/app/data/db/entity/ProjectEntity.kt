package com.uibuilder.app.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "projects",
    indices = [Index(value = ["name"], unique = false)]
)
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String = "",
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "last_modified")
    val lastModified: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "theme_id")
    val themeId: String = "default",
    @ColumnInfo(name = "typography_id")
    val typographyId: String = "default",
    @ColumnInfo(name = "rtl_mode")
    val rtlMode: Boolean = false
)
