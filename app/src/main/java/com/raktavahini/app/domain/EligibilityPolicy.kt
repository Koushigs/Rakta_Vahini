package com.raktavahini.app.domain

import java.util.concurrent.TimeUnit

object EligibilityPolicy {
    private const val MIN_DAYS_BETWEEN_DONATIONS = 90L
    private val minGapMillis = TimeUnit.DAYS.toMillis(MIN_DAYS_BETWEEN_DONATIONS)

    fun isEligible(lastDonationAtEpochMillis: Long?, nowEpochMillis: Long = System.currentTimeMillis()): Boolean {
        if (lastDonationAtEpochMillis == null) return true
        return nowEpochMillis - lastDonationAtEpochMillis >= minGapMillis
    }
}
