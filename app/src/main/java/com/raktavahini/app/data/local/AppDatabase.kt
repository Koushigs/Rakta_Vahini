package com.raktavahini.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.raktavahini.app.data.local.dao.DonationLogDao
import com.raktavahini.app.data.local.dao.DonorDao
import com.raktavahini.app.data.local.dao.LoggedInUserDao
import com.raktavahini.app.data.local.dao.SearchHistoryDao
import com.raktavahini.app.data.local.entity.DonationLogEntity
import com.raktavahini.app.data.local.entity.DonorEntity
import com.raktavahini.app.data.local.entity.LoggedInUserEntity
import com.raktavahini.app.data.local.entity.SearchHistoryEntity

@Database(
    entities = [
        DonorEntity::class,
        DonationLogEntity::class,
        LoggedInUserEntity::class,
        SearchHistoryEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun donorDao(): DonorDao
    abstract fun donationLogDao(): DonationLogDao
    abstract fun loggedInUserDao(): LoggedInUserDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}
