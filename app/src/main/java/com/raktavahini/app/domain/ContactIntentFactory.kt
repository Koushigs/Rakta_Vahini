package com.raktavahini.app.domain

import android.content.Intent
import android.net.Uri

object ContactIntentFactory {
    fun createDialIntent(phoneNumber: String): Intent {
        return Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${phoneNumber.trim()}")
        }
    }
}
