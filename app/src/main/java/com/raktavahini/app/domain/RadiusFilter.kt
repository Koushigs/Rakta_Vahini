package com.raktavahini.app.domain

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object RadiusFilter {
    private const val EARTH_RADIUS_KM = 6371.0

    fun isWithinRadius(
        originLatitude: Double,
        originLongitude: Double,
        targetLatitude: Double,
        targetLongitude: Double,
        radiusKm: Double
    ): Boolean {
        return distanceKm(
            originLatitude,
            originLongitude,
            targetLatitude,
            targetLongitude
        ) <= radiusKm
    }

    fun distanceKm(
        originLatitude: Double,
        originLongitude: Double,
        targetLatitude: Double,
        targetLongitude: Double
    ): Double {
        val latDistance = Math.toRadians(targetLatitude - originLatitude)
        val lonDistance = Math.toRadians(targetLongitude - originLongitude)
        val a = sin(latDistance / 2).let { it * it } +
            cos(Math.toRadians(originLatitude)) *
            cos(Math.toRadians(targetLatitude)) *
            sin(lonDistance / 2).let { it * it }
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }
}
