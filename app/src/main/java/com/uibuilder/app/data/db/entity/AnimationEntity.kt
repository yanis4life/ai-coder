package com.uibuilder.app.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "animations",
    foreignKeys = [
        ForeignKey(
            entity = ComponentEntity::class,
            parentColumns = ["id"],
            childColumns = ["component_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["component_id"])]
)
data class AnimationEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "component_id")
    val componentId: String,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "duration_ms")
    val durationMs: Int = 500,
    @ColumnInfo(name = "delay_ms")
    val delayMs: Int = 0,
    @ColumnInfo(name = "interpolator")
    val interpolator: String = "AccelerateDecelerate",
    @ColumnInfo(name = "repeat_count")
    val repeatCount: Int = 0,
    @ColumnInfo(name = "repeat_mode")
    val repeatMode: String = "restart",
    @ColumnInfo(name = "data_json")
    val dataJson: String = "{}",
    @ColumnInfo(name = "sequence_order")
    val sequenceOrder: Int = 0,
    @ColumnInfo(name = "sequence_mode")
    val sequenceMode: String = "sequential"
)
