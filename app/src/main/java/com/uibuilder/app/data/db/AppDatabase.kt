package com.uibuilder.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.uibuilder.app.data.db.dao.AnimationDao
import com.uibuilder.app.data.db.dao.ComponentDao
import com.uibuilder.app.data.db.dao.HistoryDao
import com.uibuilder.app.data.db.dao.ProjectDao
import com.uibuilder.app.data.db.entity.AnimationEntity
import com.uibuilder.app.data.db.entity.ComponentEntity
import com.uibuilder.app.data.db.entity.ExportHistoryEntity
import com.uibuilder.app.data.db.entity.ProjectEntity
import com.uibuilder.app.data.db.entity.VersionHistoryEntity

@Database(
    entities = [
        ProjectEntity::class,
        ComponentEntity::class,
        AnimationEntity::class,
        VersionHistoryEntity::class,
        ExportHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun projectDao(): ProjectDao
    abstract fun componentDao(): ComponentDao
    abstract fun animationDao(): AnimationDao
    abstract fun historyDao(): HistoryDao

    companion object {
        const val DATABASE_NAME = "visual_ui_builder.db"
    }
}
