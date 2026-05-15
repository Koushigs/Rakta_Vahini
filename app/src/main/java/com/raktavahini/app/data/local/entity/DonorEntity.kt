package com.raktavahini.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "donors",
    indices = [
        Index(value = ["blood_group"]),
        Index(value = ["city"]),
        Index(value = ["last_donation_at_epoch_millis"])
    ]
)
data class DonorEntity(
    @PrimaryKey
    @ColumnInfo(name = "donor_id")
    val donorId: String,
    @ColumnInfo(name = "full_name")
    val fullName: String,
    @ColumnInfo(name = "blood_group")
    val bloodGroup: String,
    @ColumnInfo(name = "city")
    val city: String,
    @ColumnInfo(name = "latitude")
    val latitude: Double,
    @ColumnInfo(name = "longitude")
    val longitude: Double,
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,
    @ColumnInfo(name = "last_donation_at_epoch_millis")
    val lastDonationAtEpochMillis: Long? = null,
    @ColumnInfo(name = "created_at_epoch_millis")
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at_epoch_millis")
    val updatedAtEpochMillis: Long = System.currentTimeMillis()
)
