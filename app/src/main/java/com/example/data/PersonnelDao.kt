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
    @Query("SELECT * FROM personnel ORDER BY returnDateMillis ASC")
    fun getAllPersonnel(): Flow<List<Personnel>>

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

    @Query("UPDATE personnel SET isReturnedToDuty = 1 WHERE id = :id")
    suspend fun confirmReturnToDuty(id: Long)

    @Query("UPDATE personnel SET startDateMillis = :startMillis, returnDateMillis = :returnMillis, isReturnedToDuty = 0 WHERE id = :id")
    suspend fun grantNewLeave(id: Long, startMillis: Long, returnMillis: Long)

    @Query("UPDATE personnel SET isPinned = :pinned, pinnedAtMillis = :pinnedAt WHERE id = :id")
    suspend fun setPinned(id: Long, pinned: Boolean, pinnedAt: Long)

    @Query("DELETE FROM personnel")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM personnel")
    fun getPersonnelCount(): Flow<Int>
}
