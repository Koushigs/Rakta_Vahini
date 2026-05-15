package com.raktavahini.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raktavahini.app.data.local.repository.DonorRepository
import com.raktavahini.app.domain.EligibilityPolicy
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DonorDirectoryViewModel(
    private val donorRepository: DonorRepository
) : ViewModel() {
    val eligibleDonors: StateFlow<List<EligibleDonorViewModel>> = donorRepository.observeDonors()
        .map { donors ->
            donors.filter { EligibilityPolicy.isEligible(it.lastDonationAtEpochMillis) }
                .map {
                    EligibleDonorViewModel(
                        donorId = it.donorId,
                        fullName = it.fullName,
                        bloodGroup = it.bloodGroup,
                        city = it.city,
                        latitude = it.latitude,
                        longitude = it.longitude
                    )
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

data class EligibleDonorViewModel(
    val donorId: String,
    val fullName: String,
    val bloodGroup: String,
    val city: String,
    val latitude: Double,
    val longitude: Double
)
