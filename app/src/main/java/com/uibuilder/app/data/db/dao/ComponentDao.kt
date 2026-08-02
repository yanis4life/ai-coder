package com.uibuilder.app.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.uibuilder.app.data.db.entity.ComponentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ComponentDao {

    @Query("SELECT * FROM components WHERE project_id = :projectId ORDER BY z_index ASC")
    fun observeByProject(projectId: String): Flow<List<ComponentEntity>>

    @Query("SELECT * FROM components WHERE project_id = :projectId ORDER BY z_index ASC")
    suspend fun getByProject(projectId: String): List<ComponentEntity>

    @Query("SELECT * FROM components WHERE id = :id")
    suspend fun getById(id: String): ComponentEntity?

    @Query("SELECT * FROM components WHERE parent_id = :parentId ORDER BY z_index ASC")
    suspend fun getByParent(parentId: String): List<ComponentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(component: ComponentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(components: List<ComponentEntity>)

    @Update
    suspend fun update(component: ComponentEntity)

    @Delete
    suspend fun delete(component: ComponentEntity)

    @Query("DELETE FROM components WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM components WHERE project_id = :projectId")
    suspend fun deleteByProject(projectId: String)

    @Query("UPDATE components SET z_index = :zIndex, updated_at = :timestamp WHERE id = :id")
    suspend fun updateZIndex(id: String, zIndex: Int, timestamp: Long)

    @Query("UPDATE components SET parent_id = :parentId, updated_at = :timestamp WHERE id = :id")
    suspend fun updateParent(id: String, parentId: String?, timestamp: Long)

    @Query("SELECT MAX(z_index) FROM components WHERE project_id = :projectId")
    suspend fun getMaxZIndex(projectId: String): Int?
}
