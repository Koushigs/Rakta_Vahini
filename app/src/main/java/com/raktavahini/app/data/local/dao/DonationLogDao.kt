package com.raktavahini.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.raktavahini.app.data.local.entity.DonationLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationLogDao {
    @Query("SELECT * FROM donation_logs ORDER BY donated_at_epoch_millis DESC")
    fun observeDonationLogs(): Flow<List<DonationLogEntity>>

    @Query("SELECT * FROM donation_logs WHERE donor_id = :donorId ORDER BY donated_at_epoch_millis DESC LIMIT 1")
    suspend fun findLatestDonationForDonor(donorId: String): DonationLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonationLog(donationLog: DonationLogEntity): Long
}
