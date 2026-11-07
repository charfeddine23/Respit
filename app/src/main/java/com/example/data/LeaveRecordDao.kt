package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaveRecordDao {
    @Query("SELECT * FROM leave_records ORDER BY returnDateMillis DESC")
    fun getAllLeaves(): Flow<List<LeaveRecord>>

    @Query("SELECT * FROM leave_records WHERE personnelId = :personnelId ORDER BY returnDateMillis DESC")
    fun getLeavesForPersonnel(personnelId: Long): Flow<List<LeaveRecord>>

    @Query("SELECT * FROM leave_records WHERE id = :id")
    suspend fun getLeaveById(id: Long): LeaveRecord?

    @Query("SELECT COUNT(*) FROM leave_records")
    fun getLeavesCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(leave: LeaveRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(leaves: List<LeaveRecord>)

    @Update
    suspend fun update(leave: LeaveRecord)

    @Delete
    suspend fun delete(leave: LeaveRecord)

    @Query("DELETE FROM leave_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM leave_records")
    suspend fun clearAll()
}
