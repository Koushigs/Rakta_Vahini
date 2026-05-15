package com.raktavahini.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.raktavahini.app.data.local.entity.DonorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DonorDao {
    @Query("SELECT * FROM donors ORDER BY full_name ASC")
    fun observeAllDonors(): Flow<List<DonorEntity>>

    @Query("SELECT * FROM donors WHERE donor_id = :donorId LIMIT 1")
    suspend fun findDonorById(donorId: String): DonorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDonor(donor: DonorEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDonors(donors: List<DonorEntity>)

    @Update
    suspend fun updateDonor(donor: DonorEntity)

    @Delete
    suspend fun deleteDonor(donor: DonorEntity)
}
