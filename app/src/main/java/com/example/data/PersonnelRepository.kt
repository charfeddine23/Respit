package com.example.data

import kotlinx.coroutines.flow.Flow

class PersonnelRepository(private val dao: PersonnelDao) {
    val allPersonnel: Flow<List<Personnel>> = dao.getAllPersonnel()

    suspend fun insert(personnel: Personnel): Long {
        return dao.insert(personnel)
    }

    suspend fun update(personnel: Personnel) {
        dao.update(personnel)
    }

    suspend fun delete(personnel: Personnel) {
        dao.delete(personnel)
    }

    suspend fun confirmReturnToDuty(id: Long) {
        dao.confirmReturnToDuty(id)
    }

    suspend fun grantNewLeave(id: Long, startMillis: Long, returnMillis: Long) {
        dao.grantNewLeave(id, startMillis, returnMillis)
    }

    suspend fun insertAll(list: List<Personnel>) {
        dao.insertAll(list)
    }

    suspend fun getPersonnelById(id: Long): Personnel? {
        return dao.getPersonnelById(id)
    }

    suspend fun clearAndReset(sampleList: List<Personnel>) {
        dao.clearAll()
        dao.insertAll(sampleList)
    }
}
