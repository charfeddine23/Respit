package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonnelDao {
    @Query("SELECT * FROM personnel WHERE isDeleted = 0 ORDER BY isPinned DESC, pinnedAtMillis DESC, returnDateMillis ASC")
    fun getAllPersonnel(): Flow<List<Personnel>>

    @Query("SELECT * FROM personnel WHERE isDeleted = 1 ORDER BY deletedAtMillis DESC")
    fun getTrashedPersonnel(): Flow<List<Personnel>>

    @Query("SELECT COUNT(*) FROM personnel WHERE isDeleted = 1")
    fun getTrashedCount(): Flow<Int>

    @Query("SELECT * FROM personnel WHERE id = :id")
    suspend fun getPersonnelById(id: Long): Personnel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(personnel: Personnel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Personnel>)

    @Update
    suspend fun update(personnel: Personnel)

    @Delete
    suspend fun delete(personnel: Personnel)

    @Query("DELETE FROM personnel WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE personnel SET isDeleted = 1, deletedAtMillis = :deletedAt WHERE id = :id")
    suspend fun moveToTrash(id: Long, deletedAt: Long)

    @Query("UPDATE personnel SET isDeleted = 0, deletedAtMillis = 0 WHERE id = :id")
    suspend fun restoreFromTrash(id: Long)

    @Query("UPDATE personnel SET isDeleted = 0, deletedAtMillis = 0 WHERE isDeleted = 1")
    suspend fun restoreAllFromTrash()

    @Query("DELETE FROM personnel WHERE isDeleted = 1")
    suspend fun emptyTrash()

    @Query("UPDATE personnel SET isReturnedToDuty = 1 WHERE id = :id")
    suspend fun confirmReturnToDuty(id: Long)

    @Query("UPDATE personnel SET startDateMillis = :startMillis, returnDateMillis = :returnMillis, isReturnedToDuty = 0 WHERE id = :id")
    suspend fun grantNewLeave(id: Long, startMillis: Long, returnMillis: Long)

    @Query("UPDATE personnel SET isPinned = :pinned, pinnedAtMillis = :pinnedAt WHERE id = :id")
    suspend fun setPinned(id: Long, pinned: Boolean, pinnedAt: Long)

    @Query("DELETE FROM personnel WHERE isDeleted = 0")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM personnel WHERE isDeleted = 0")
    fun getPersonnelCount(): Flow<Int>
}
