package com.raktavahini.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "donation_logs",
    foreignKeys = [
        ForeignKey(
            entity = DonorEntity::class,
            parentColumns = ["donor_id"],
            childColumns = ["donor_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["donor_id"]),
        Index(value = ["donated_at_epoch_millis"])
    ]
)
data class DonationLogEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "donation_log_id")
    val donationLogId: Long = 0,
    @ColumnInfo(name = "donor_id")
    val donorId: String,
    @ColumnInfo(name = "donated_at_epoch_millis")
    val donatedAtEpochMillis: Long,
    @ColumnInfo(name = "blood_unit_count")
    val bloodUnitCount: Int = 1,
    @ColumnInfo(name = "notes")
    val notes: String? = null
)
