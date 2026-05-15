package com.raktavahini.app.data.local.repository

import com.raktavahini.app.data.local.dao.DonationLogDao
import com.raktavahini.app.data.local.dao.DonorDao
import com.raktavahini.app.data.local.dao.SearchHistoryDao
import com.raktavahini.app.data.local.entity.DonationLogEntity
import com.raktavahini.app.data.local.entity.DonorEntity
import com.raktavahini.app.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

class RoomDonorRepository(
    private val donorDao: DonorDao,
    private val donationLogDao: DonationLogDao,
    private val searchHistoryDao: SearchHistoryDao
) : DonorRepository {
    override fun observeDonors(): Flow<List<DonorEntity>> = donorDao.observeAllDonors()

    override fun observeDonationLogs(): Flow<List<DonationLogEntity>> = donationLogDao.observeDonationLogs()

    override fun observeSearchHistory(): Flow<List<SearchHistoryEntity>> = searchHistoryDao.observeSearchHistory()

    override suspend fun upsertDonor(donor: DonorEntity) {
        donorDao.upsertDonor(donor)
    }

    override suspend fun upsertDonors(donors: List<DonorEntity>) {
        donorDao.upsertDonors(donors)
    }

    override suspend fun logDonation(donationLog: DonationLogEntity): Long {
        return donationLogDao.insertDonationLog(donationLog)
    }

    override suspend fun recordSearch(searchHistory: SearchHistoryEntity): Long {
        return searchHistoryDao.insertSearchHistory(searchHistory)
    }
}
