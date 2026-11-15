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

    suspend fun setPinned(id: Long, pinned: Boolean, pinnedAt: Long) {
        personnelDao.setPinned(id, pinned, pinnedAt)
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

    suspend fun countExactLeave(personnelId: Long, startMillis: Long, returnMillis: Long): Int {
        return leaveRecordDao.countExactLeave(personnelId, startMillis, returnMillis)
    }

    suspend fun countExactLeaveByReg(regNo: String, startMillis: Long, returnMillis: Long): Int {
        return leaveRecordDao.countExactLeaveByReg(regNo, startMillis, returnMillis)
    }

    suspend fun updateLeaveStatus(personnelId: Long, startMillis: Long, returnMillis: Long, statusText: String) {
        leaveRecordDao.updateLeaveStatus(personnelId, startMillis, returnMillis, statusText)
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

    // --- Synchronous/Bulk Maintenance (Never deletes leave_records) ---
    suspend fun clearAndReset(
        samplePersonnel: List<Personnel>,
        sampleLeaves: List<LeaveRecord> = emptyList()
    ) {
        personnelDao.clearAll()
        personnelDao.insertAll(samplePersonnel)
        if (sampleLeaves.isNotEmpty()) {
            leaveRecordDao.insertAll(sampleLeaves)
        }
    }
}
