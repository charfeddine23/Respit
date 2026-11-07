package com.example.data

import kotlinx.coroutines.flow.Flow

class PersonnelRepository(
    private val personnelDao: PersonnelDao,
    private val leaveRecordDao: LeaveRecordDao
) {
    // Flow streams for reactive UI
    val allPersonnel: Flow<List<Personnel>> = personnelDao.getAllPersonnel()
    val allLeaves: Flow<List<LeaveRecord>> = leaveRecordDao.getAllLeaves()
    val personnelCount: Flow<Int> = personnelDao.getPersonnelCount()
    val leavesCount: Flow<Int> = leaveRecordDao.getLeavesCount()

    // --- Personnel Operations ---
    suspend fun insert(personnel: Personnel): Long {
        return personnelDao.insert(personnel)
    }

    suspend fun update(personnel: Personnel) {
        personnelDao.update(personnel)
    }

    suspend fun delete(personnel: Personnel) {
        personnelDao.delete(personnel)
    }

    suspend fun deleteById(id: Long) {
        personnelDao.deleteById(id)
    }

    suspend fun confirmReturnToDuty(id: Long) {
        personnelDao.confirmReturnToDuty(id)
    }

    suspend fun grantNewLeave(id: Long, startMillis: Long, returnMillis: Long) {
        personnelDao.grantNewLeave(id, startMillis, returnMillis)
    }

    suspend fun insertAll(list: List<Personnel>) {
        personnelDao.insertAll(list)
    }

    suspend fun getPersonnelById(id: Long): Personnel? {
        return personnelDao.getPersonnelById(id)
    }

    suspend fun clearPersonnel() {
        personnelDao.clearAll()
    }

    // --- LeaveRecord Operations ---
    fun getLeavesForPersonnel(personnelId: Long): Flow<List<LeaveRecord>> {
        return leaveRecordDao.getLeavesForPersonnel(personnelId)
    }

    suspend fun insertLeave(leave: LeaveRecord): Long {
        return leaveRecordDao.insert(leave)
    }

    suspend fun insertAllLeaves(leaves: List<LeaveRecord>) {
        leaveRecordDao.insertAll(leaves)
    }

    suspend fun updateLeave(leave: LeaveRecord) {
        leaveRecordDao.update(leave)
    }

    suspend fun deleteLeave(leave: LeaveRecord) {
        leaveRecordDao.delete(leave)
    }

    suspend fun deleteLeaveById(id: Long) {
        leaveRecordDao.deleteById(id)
    }

    suspend fun clearLeaves() {
        leaveRecordDao.clearAll()
    }

    // --- Synchronous/Bulk Maintenance ---
    suspend fun clearAndReset(
        samplePersonnel: List<Personnel>,
        sampleLeaves: List<LeaveRecord> = emptyList()
    ) {
        personnelDao.clearAll()
        personnelDao.insertAll(samplePersonnel)
        leaveRecordDao.clearAll()
        if (sampleLeaves.isNotEmpty()) {
            leaveRecordDao.insertAll(sampleLeaves)
        }
    }
}
