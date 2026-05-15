package com.raktavahini.app.data.local.repository

import com.raktavahini.app.data.local.entity.DonationLogEntity
import com.raktavahini.app.data.local.entity.DonorEntity
import com.raktavahini.app.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

interface DonorRepository {
    fun observeDonors(): Flow<List<DonorEntity>>
    fun observeDonationLogs(): Flow<List<DonationLogEntity>>
    fun observeSearchHistory(): Flow<List<SearchHistoryEntity>>

    suspend fun upsertDonor(donor: DonorEntity)
    suspend fun upsertDonors(donors: List<DonorEntity>)
    suspend fun logDonation(donationLog: DonationLogEntity): Long
    suspend fun recordSearch(searchHistory: SearchHistoryEntity): Long
}
