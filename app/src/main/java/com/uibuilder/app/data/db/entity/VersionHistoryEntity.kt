package com.uibuilder.app.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "version_history",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["project_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["project_id"]), Index(value = ["branch_name"])]
)
data class VersionHistoryEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "project_id")
    val projectId: String,
    @ColumnInfo(name = "message")
    val message: String,
    @ColumnInfo(name = "snapshot_json")
    val snapshotJson: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "branch_name")
    val branchName: String = "main",
    @ColumnInfo(name = "parent_id")
    val parentId: String? = null
)
