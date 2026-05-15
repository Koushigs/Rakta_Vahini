package com.raktavahini.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.raktavahini.app.ui.RaktaVahiniApp
import com.raktavahini.app.ui.theme.RaktaVahiniTheme
import com.raktavahini.app.ui.auth.AuthStore
import com.raktavahini.app.ui.auth.UserProfile

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if launched from web intent
        val action = intent?.action
        val data = intent?.data
        if (action == Intent.ACTION_VIEW && data?.scheme == "raktavahini" && data.host == "open") {
            AuthStore.setLoggedIn(this, true)
            if (AuthStore.getProfile(this) == null) {
                AuthStore.saveProfile(
                    this, 
                    UserProfile(
                        firstName = "Guest", 
                        lastName = "User", 
                        bloodGroup = "O+", 
                        dateOfBirth = "01/01/2000", 
                        currentLocation = "Unknown Location", 
                        phoneNumber = "9999999999", 
                        email = "guest@example.com"
                    )
                )
            }
        }

        setContent {
            RaktaVahiniTheme {
                RaktaVahiniApp()
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun MainActivityPreview() {
    RaktaVahiniTheme {
        RaktaVahiniApp()
    }
}
