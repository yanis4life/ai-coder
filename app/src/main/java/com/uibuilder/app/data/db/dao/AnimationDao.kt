package com.uibuilder.app.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.uibuilder.app.data.db.entity.AnimationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimationDao {

    @Query("SELECT * FROM animations WHERE component_id = :componentId ORDER BY sequence_order ASC")
    fun observeByComponent(componentId: String): Flow<List<AnimationEntity>>

    @Query("SELECT * FROM animations WHERE component_id = :componentId ORDER BY sequence_order ASC")
    suspend fun getByComponent(componentId: String): List<AnimationEntity>

    @Query("SELECT * FROM animations WHERE id = :id")
    suspend fun getById(id: String): AnimationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(animation: AnimationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(animations: List<AnimationEntity>)

    @Update
    suspend fun update(animation: AnimationEntity)

    @Delete
    suspend fun delete(animation: AnimationEntity)

    @Query("DELETE FROM animations WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM animations WHERE component_id = :componentId")
    suspend fun deleteByComponent(componentId: String)
}
