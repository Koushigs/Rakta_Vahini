package com.raktavahini.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.raktavahini.app.data.local.entity.LoggedInUserEntity

@Dao
interface LoggedInUserDao {
    @Query("SELECT * FROM logged_in_users WHERE is_current_session = 1 ORDER BY updated_at_epoch_millis DESC LIMIT 1")
    suspend fun getCurrentLoggedInUser(): LoggedInUserEntity?

    @Query("SELECT COUNT(*) FROM logged_in_users WHERE is_current_session = 1")
    suspend fun getCurrentSessionCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoggedInUser(user: LoggedInUserEntity): Long

    @Query("UPDATE logged_in_users SET is_current_session = 0, updated_at_epoch_millis = :updatedAt WHERE is_current_session = 1")
    suspend fun clearCurrentSession(updatedAt: Long = System.currentTimeMillis())
}