package com.raktavahini.app.ui.auth

import android.content.Context
import com.raktavahini.app.data.local.AppDatabaseProvider
import com.raktavahini.app.data.local.entity.LoggedInUserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

data class UserProfile(
    val firstName: String,
    val lastName: String,
    val bloodGroup: String,
    val dateOfBirth: String,
    val currentLocation: String,
    val phoneNumber: String = "",
    val email: String = ""
)

object AuthStore {
    fun isLoggedIn(context: Context): Boolean {
        return runBlocking {
            withContext(Dispatchers.IO) {
                AppDatabaseProvider.get(context).loggedInUserDao().getCurrentSessionCount() > 0
            }
        }
    }

    fun setLoggedIn(context: Context, loggedIn: Boolean) {
        runBlocking {
            withContext(Dispatchers.IO) {
                val dao = AppDatabaseProvider.get(context).loggedInUserDao()
                if (!loggedIn) {
                    dao.clearCurrentSession()
                }
            }
        }
    }

    fun clearSession(context: Context) {
        runBlocking {
            withContext(Dispatchers.IO) {
                AppDatabaseProvider.get(context).loggedInUserDao().clearCurrentSession()
            }
        }
    }

    fun saveProfile(context: Context, profile: UserProfile) {
        runBlocking {
            withContext(Dispatchers.IO) {
                val dao = AppDatabaseProvider.get(context).loggedInUserDao()
                dao.clearCurrentSession()
                dao.insertLoggedInUser(
                    LoggedInUserEntity(
                        firstName = profile.firstName,
                        lastName = profile.lastName,
                        bloodGroup = profile.bloodGroup,
                        dateOfBirth = profile.dateOfBirth,
                        currentLocation = profile.currentLocation,
                        phoneNumber = profile.phoneNumber,
                        email = profile.email,
                        isCurrentSession = true
                    )
                )
            }
        }
    }

    fun getProfile(context: Context): UserProfile? {
        return runBlocking {
            withContext(Dispatchers.IO) {
                AppDatabaseProvider.get(context).loggedInUserDao().getCurrentLoggedInUser()?.let { user ->
                    UserProfile(
                        firstName = user.firstName,
                        lastName = user.lastName,
                        bloodGroup = user.bloodGroup,
                        dateOfBirth = user.dateOfBirth,
                        currentLocation = user.currentLocation,
                        phoneNumber = user.phoneNumber,
                        email = user.email
                    )
                }
            }
        }
    }
}
