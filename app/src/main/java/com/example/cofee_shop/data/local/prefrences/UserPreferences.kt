package com.example.cofee_shop.data.local.prefrences

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class UserPreferences @Inject constructor(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "brew_buddy_prefs"
        private const val KEY_USERNAME = "username"
        private const val KEY_FIRST_LAUNCH = "first_launch"
    }

    fun saveUsername(username: String) {
        sharedPreferences.edit {
            putString(KEY_USERNAME, username)
                .putBoolean(KEY_FIRST_LAUNCH, false)
        }
    }

    fun getUsername(): String? {
        return sharedPreferences.getString(KEY_USERNAME, null)
    }

    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun clearUserData() {
        sharedPreferences.edit { clear() }
    }
}