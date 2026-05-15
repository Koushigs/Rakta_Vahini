package com.raktavahini.app.location

import android.location.Location
import com.google.android.gms.location.LocationRequest
import kotlinx.coroutines.flow.Flow

interface FusedLocationGateway {
    suspend fun getLastKnownLocation(): Location?
    fun observeLocationUpdates(locationRequest: LocationRequest): Flow<Location>
}
