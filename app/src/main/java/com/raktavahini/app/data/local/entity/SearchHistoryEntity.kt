package com.raktavahini.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "search_id")
    val searchId: Long = 0,
    @ColumnInfo(name = "query_text")
    val queryText: String,
    @ColumnInfo(name = "radius_km")
    val radiusKm: Int,
    @ColumnInfo(name = "result_count")
    val resultCount: Int,
    @ColumnInfo(name = "searched_at_epoch_millis")
    val searchedAtEpochMillis: Long = System.currentTimeMillis()
)
