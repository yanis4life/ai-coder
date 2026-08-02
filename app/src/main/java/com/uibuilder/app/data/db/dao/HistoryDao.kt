package com.uibuilder.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.uibuilder.app.data.db.entity.ExportHistoryEntity
import com.uibuilder.app.data.db.entity.VersionHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM version_history WHERE project_id = :projectId ORDER BY created_at DESC")
    fun observeVersionHistory(projectId: String): Flow<List<VersionHistoryEntity>>

    @Query("SELECT * FROM version_history WHERE id = :id")
    suspend fun getVersionById(id: String): VersionHistoryEntity?

    @Query("SELECT * FROM version_history WHERE project_id = :projectId AND branch_name = :branch ORDER BY created_at DESC LIMIT 1")
    suspend fun getLatestVersionOnBranch(projectId: String, branch: String): VersionHistoryEntity?

    @Query("SELECT DISTINCT branch_name FROM version_history WHERE project_id = :projectId")
    suspend fun getBranches(projectId: String): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(version: VersionHistoryEntity)

    @Query("DELETE FROM version_history WHERE id = :id")
    suspend fun deleteVersion(id: String)

    @Query("DELETE FROM version_history WHERE project_id = :projectId")
    suspend fun deleteAllForProject(projectId: String)

    @Query("SELECT * FROM export_history ORDER BY created_at DESC LIMIT 100")
    fun observeExportHistory(): Flow<List<ExportHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExport(entry: ExportHistoryEntity)

    @Query("DELETE FROM export_history WHERE id = :id")
    suspend fun deleteExport(id: String)

    @Query("SELECT COUNT(*) FROM export_history WHERE project_id = :projectId")
    suspend fun getExportCount(projectId: String): Int

    @Query("SELECT SUM(file_size_bytes) FROM export_history WHERE success = 1")
    suspend fun getTotalExportedBytes(): Long?
}
